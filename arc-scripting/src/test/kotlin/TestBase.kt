// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.scripting

import io.github.lmos.arc.agents.Agent
import io.github.lmos.arc.agents.dsl.AgentDefinitionContext
import io.github.lmos.arc.agents.dsl.BasicAgentDefinitionContext
import io.github.lmos.arc.agents.dsl.ChatAgentFactory
import io.github.lmos.arc.agents.dsl.CoroutineBeanProvider
import io.github.lmos.arc.agents.llm.ChatCompleter
import io.github.lmos.arc.agents.llm.ChatCompleterProvider
import io.github.lmos.arc.core.getOrThrow
import io.github.lmos.arc.scripting.agents.KtsAgentScriptEngine
import io.github.lmos.arc.scripting.agents.ScriptingAgentLoader
import io.github.lmos.arc.scripting.functions.KtsFunctionScriptEngine
import io.github.lmos.arc.scripting.functions.ScriptingLLMFunctionLoader
import io.mockk.clearMocks
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import java.io.File

open class TestBase {

    val scripts = File("tmp-scripts")
    val functionEngine = KtsFunctionScriptEngine()
    val agentEngine = KtsAgentScriptEngine()
    val testBeanProvider = CoroutineBeanProvider()
    val testAgentFactory = ChatAgentFactory(testBeanProvider)
    val testContext = BasicAgentDefinitionContext(testAgentFactory)
    val chatCompleter = mockk<ChatCompleter>()
    val chatCompleterProvider = ChatCompleterProvider { chatCompleter }
    val scriptingLLMFunctionLoader = ScriptingLLMFunctionLoader(testBeanProvider, functionEngine)
    val scriptingAgentLoader = ScriptingAgentLoader(testAgentFactory, agentEngine)

    fun eval(script: String, context: AgentDefinitionContext = testContext): Agent<*, *> {
        agentEngine.eval(readScript(script), context).getOrThrow()
        return testContext.agents.first()
    }

    @BeforeEach
    fun setup() {
        clearMocks(chatCompleter)
        scripts.mkdirs()
    }

    @AfterEach
    fun clean() {
        scripts.deleteRecursively()
    }
}
