// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.scripting.functions

import io.github.lmos.arc.agents.dsl.BasicFunctionDefinitionContext
import io.github.lmos.arc.agents.dsl.BeanProvider
import io.github.lmos.arc.agents.functions.LLMFunction
import io.github.lmos.arc.agents.functions.LLMFunctionLoader
import io.github.lmos.arc.core.Result
import io.github.lmos.arc.core.Success
import io.github.lmos.arc.scripting.ScriptFailedException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import java.io.File
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.script.experimental.api.ResultValue

class ScriptingLLMFunctionLoader(
    private val beanProvider: BeanProvider,
    private val functionScriptEngine: FunctionScriptEngine,
) : LLMFunctionLoader {

    private val log = LoggerFactory.getLogger(javaClass)
    private val functions = ConcurrentHashMap<String, LLMFunction>()
    private val lastModified = ConcurrentHashMap<String, Long>()
    private val scope = CoroutineScope(SupervisorJob())
    private val running = AtomicBoolean(false)

    override fun provideByGroup(functionGroup: String): List<LLMFunction> {
        return functions.values.filter { it.group == functionGroup }.toList()
    }

    fun deleteFunctions() {
        functions.clear()
    }

    fun startHotReload(functionsFolder: File, hotReloadDelay: Duration = Duration.ofMinutes(3)) {
        running.set(true)
        scope.launch {
            while (running.get()) {
                loadFunctions(functionsFolder)
                delay(hotReloadDelay.toMillis())
            }
        }
    }

    fun destroy() {
        running.set(false)
        scope.cancel()
    }

    fun loadFunction(agentScript: String): Result<ResultValue?, ScriptFailedException> {
        val context = BasicFunctionDefinitionContext(beanProvider)
        val result = functionScriptEngine.eval(agentScript, context)

        if (result is Success && context.functions.isNotEmpty()) {
            log.info("Discovered the following llm functions (scripting): ${context.functions}")
            functions.clear()
            functions.putAll(context.functions.associateBy { "${it.group}::${it.name}" })
        }
        return result
    }

    fun loadFunctions(functionsFolder: File) {
        val discoveredFunctions = functionsFolder.listFiles()
            ?.filter { it.name.endsWith(".functions.kts") }
            ?.filter { it.lastModified() != lastModified[it.name] }
            ?.map {
                lastModified[it.name] = it.lastModified()
                it
            }
            ?.map { it.readText() }
            ?.flatMap { script ->
                val context = BasicFunctionDefinitionContext(beanProvider)
                functionScriptEngine.eval(script, context)
                context.functions
            }
            ?.associateBy { "${it.group}::${it.name}" }
            ?: emptyMap()
        if (discoveredFunctions.isNotEmpty()) {
            log.info("Discovered the following llm functions (scripting): $discoveredFunctions")
        }
        functions.putAll(discoveredFunctions)
    }
}
