// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.scripting

import ai.ancf.lmos.arc.agents.dsl.BasicFunctionDefinitionContext
import ai.ancf.lmos.arc.agents.dsl.DSLContext
import ai.ancf.lmos.arc.agents.dsl.FunctionDefinitionContext
import ai.ancf.lmos.arc.core.getOrThrow
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test

class ContextTest : TestBase() {

    @Test
    fun `test that context receivers are supported in agents`(): Unit = runBlocking {
        val script = """
            import ai.ancf.lmos.arc.scripting.ContextFilter
            agent {
                name = "weather"
                description = ""
                systemPrompt = { "" }
                filterInput {
                     ContextFilter()
                }
            }
        """
        agentEngine.eval(script, testContext).getOrThrow()
    }

    @Test
    fun `test that context receivers are supported in functions`(): Unit = runBlocking {
        val context = BasicFunctionDefinitionContext(testBeanProvider)
        val script = """
           import ai.ancf.lmos.arc.scripting.ContextFilter
           function(
                name = "get_weather",
                description = ""
            ) { 
                ContextFilter()
                "out"
            }
        """
        functionEngine.eval(script, context).getOrThrow()
    }
}

context(DSLContext)
class ContextFilter

context(FunctionDefinitionContext)
class ContextFunction
