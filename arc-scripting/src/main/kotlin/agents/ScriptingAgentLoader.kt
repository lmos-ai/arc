// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.scripting.agents

import io.github.lmos.arc.agents.Agent
import io.github.lmos.arc.agents.AgentLoader
import io.github.lmos.arc.agents.dsl.AgentFactory
import io.github.lmos.arc.agents.dsl.BasicAgentDefinitionContext
import io.github.lmos.arc.core.Result
import io.github.lmos.arc.core.Success
import io.github.lmos.arc.core.onFailure
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

/**
 * Provider for agents defined in Agent/Function scripts.
 * This provider has a hot-reload feature that will reload agents from the given folder in short intervals.
 */
class ScriptingAgentLoader(
    private val agentFactory: AgentFactory<*>,
    private val agentScriptEngine: AgentScriptEngine,
) : AgentLoader {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val agents = ConcurrentHashMap<String, Agent<*, *>>()
    private val lastModified = ConcurrentHashMap<String, Long>()
    private val scope = CoroutineScope(SupervisorJob())
    private val running = AtomicBoolean(false)

    override fun getAgents() = agents.values.toList()

    fun getAgentByName(name: String) = agents[name]

    fun loadAgent(agentScript: String): Result<ResultValue?, ScriptFailedException> {
        val context = BasicAgentDefinitionContext(agentFactory)
        val result = agentScriptEngine.eval(agentScript, context)
        if (result is Success && context.agents.isNotEmpty()) {
            log.info("Discovered the following agents (scripting): ${context.agents}")
            agents.clear()
            agents.putAll(context.agents.associateBy { it.name })
        }
        return result
    }

    fun loadCompiledAgent(compiledAgentScript: CompiledAgentLoader) {
        val context = BasicAgentDefinitionContext(agentFactory)
        compiledAgentScript.load(context)
        if (context.agents.isNotEmpty()) {
            log.info("Discovered the following agents (compiled): ${context.agents}")
            agents.clear()
            agents.putAll(context.agents.associateBy { it.name })
        }
    }

    fun startHotReload(agentsFolder: File, hotReloadDelay: Duration = Duration.ofMinutes(3)) {
        log.debug("Starting hot-reload of agents from ${agentsFolder.absoluteFile} which has ${agentsFolder.listFiles()?.size} files")
        running.set(true)
        scope.launch {
            while (running.get()) {
                loadAgents(agentsFolder)
                delay(hotReloadDelay.toMillis())
            }
        }
    }

    fun destroy() {
        running.set(false)
        scope.cancel()
    }

    private fun loadAgents(agentsFolder: File) {
        val discoveredAgents = agentsFolder.listFiles()
            ?.filter { it.name.endsWith(".agent.kts") }
            ?.filter { it.lastModified() != lastModified[it.name] }
            ?.map {
                lastModified[it.name] = it.lastModified()
                it
            }
            ?.map { it.name to it.readText() }
            ?.flatMap { script ->
                val context = BasicAgentDefinitionContext(agentFactory)
                agentScriptEngine.eval(script.second, context).onFailure { ex ->
                    log.error("Failed to load script ${script.first}!", ex)
                }
                context.agents
            }
            ?.associateBy { it.name }
            ?: emptyMap()
        if (discoveredAgents.isNotEmpty()) {
            log.info("Discovered the following agents (scripting): $discoveredAgents")
        }
        agents.putAll(discoveredAgents)
    }
}
