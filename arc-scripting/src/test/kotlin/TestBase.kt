// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.scripting

import ai.ancf.lmos.arc.agents.Agent
import ai.ancf.lmos.arc.agents.dsl.AgentDefinitionContext
import ai.ancf.lmos.arc.agents.dsl.BasicAgentDefinitionContext
import ai.ancf.lmos.arc.agents.dsl.ChatAgentFactory
import ai.ancf.lmos.arc.agents.dsl.CoroutineBeanProvider
import ai.ancf.lmos.arc.agents.llm.ChatCompleter
import ai.ancf.lmos.arc.agents.llm.ChatCompleterProvider
import ai.ancf.lmos.arc.core.getOrThrow
import ai.ancf.lmos.arc.scripting.agents.KtsAgentScriptEngine
import ai.ancf.lmos.arc.scripting.agents.ScriptingAgentLoader
import ai.ancf.lmos.arc.scripting.functions.KtsFunctionScriptEngine
import ai.ancf.lmos.arc.scripting.functions.ScriptingLLMFunctionLoader
import io.mockk.clearMocks
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import java.io.File

open class TestBase {

    val scripts = File("tmp-scripts")
    val scriptsSubFolder = File(scripts, "subFolder")

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
        scriptsSubFolder.mkdirs()
    }

    @AfterEach
    fun clean() {
        scripts.deleteRecursively()
    }
}
