// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package org.eclipse.lmos.arc.agents.dsl

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.eclipse.lmos.arc.agents.ChatAgent
import org.eclipse.lmos.arc.agents.TestBase
import org.junit.jupiter.api.Test

class AgentDeveloperPromptTest : TestBase() {

    @Test
    fun `test DeveloperPrompt output`(): Unit = runBlocking {
        val agent = agent {
            name = ""
            description = ""
            developerPrompt = {
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
    fun `test developerPrompt has access to context beans`(): Unit = runBlocking {
        val testString = "testString"
        val agent = agent {
            name = ""
            description = ""
            developerPrompt = {
                get<String>()
            }
        }
        testBeanProvider.setContext(setOf(testString)) {
            val (input, _) = executeAgent(agent as ChatAgent, "bad stuff here")
            assertThat(input.first().content).isEqualTo("testString")
        }
    }
}
