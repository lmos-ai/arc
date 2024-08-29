// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.agents.dsl

import ai.ancf.lmos.arc.agents.ChatAgent
import ai.ancf.lmos.arc.agents.TestBase
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AgentSystemPromptTest : TestBase() {

    @Test
    fun `test SystemPrompt output`(): Unit = runBlocking {
        val agent = agent {
            name = ""
            description = ""
            systemPrompt = {
                +"""first"""
                if (false) +"error"
                if (true) +"second"
                """third"""
            }
        }
        val (input, _) = executeAgent(agent as ChatAgent, "bad stuff here")
        assertThat(input.first().content).isEqualTo("firstsecondthird")
    }

    @Test
    fun `test SystemPrompt has access to context beans`(): Unit = runBlocking {
        val testString = "testString"
        val agent = agent {
            name = ""
            description = ""
            systemPrompt = {
                get<String>()
            }
        }
        testBeanProvider.setContext(setOf(testString)) {
            val (input, _) = executeAgent(agent as ChatAgent, "bad stuff here")
            assertThat(input.first().content).isEqualTo("testString")
        }
    }
}
