// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.agents.dsl

import ai.ancf.lmos.arc.agents.ChatAgent
import ai.ancf.lmos.arc.agents.TestBase
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AgentFilterTest : TestBase() {

    @Test
    fun `test input filter - string replace`(): Unit = runBlocking {
        val agent = agent {
            name = ""
            description = ""
            systemPrompt = { "" }
            filterInput {
                "good" replaces "bad"
                "things" replaces "stuff"
            }
        }
        val (input, _) = executeAgent(agent as ChatAgent, "bad stuff")
        assertThat(input.last().content).isEqualTo("good things")
    }

    @Test
    fun `test output filter - string replace`(): Unit = runBlocking {
        val agent = agent {
            name = ""
            description = ""
            systemPrompt = { "" }
            filterOutput {
                "good" replaces "bad"
                "things" replaces "stuff"
            }
        }
        val (_, output) = executeAgent(agent as ChatAgent, "question", "bad stuff")
        assertThat(output.transcript.last().content).isEqualTo("good things")
    }

    @Test
    fun `test input filter - string remove using '-'`(): Unit = runBlocking {
        val agent = agent {
            name = ""
            description = ""
            systemPrompt = { "" }
            filterInput {
                -"bad"
                -"stuff"
            }
        }
        val (input, _) = executeAgent(agent as ChatAgent, "bad stuff here")
        assertThat(input.last().content).isEqualTo("  here")
    }

    @Test
    fun `test output filter - string remove using '-'`(): Unit = runBlocking {
        val agent = agent {
            name = ""
            description = ""
            systemPrompt = { "" }
            filterOutput {
                -"bad"
                -"stuff"
            }
        }
        val (_, output) = executeAgent(agent as ChatAgent, "question", "bad stuff here")
        assertThat(output.transcript.last().content).isEqualTo("  here")
    }

    @Test
    fun `test input filter - NumberFilter`(): Unit = runBlocking {
        val agent = agent {
            name = ""
            description = ""
            systemPrompt = { "" }
            filterInput {
                +NumberFilter()
            }
        }
        val (input, _) = executeAgent(agent as ChatAgent, "1234", "Bot")
        assertThat(input.last().content).isEqualTo("NUMBER")
    }

    @Test
    fun `test input filter - NumberFilter from context`(): Unit = runBlocking {
        val agent = agent {
            name = ""
            description = ""
            systemPrompt = { "" }
            filterInput {
                +NumberFilter::class
            }
        }
        val (input, _) = executeAgent(agent as ChatAgent, "1234", "Bot")
        assertThat(input.last().content).isEqualTo("NUMBER")
    }

    @Test
    fun `test output filter - NumberFilter`(): Unit = runBlocking {
        val agent = agent {
            name = ""
            description = ""
            systemPrompt = { "" }
            filterOutput {
                +NumberFilter()
            }
        }
        val (_, output) = executeAgent(agent as ChatAgent, "hello", "1234")
        assertThat(output.transcript.last().content).isEqualTo("NUMBER")
    }

    @Test
    fun `test output filter - NumberFilter from context`(): Unit = runBlocking {
        val agent = agent {
            name = ""
            description = ""
            systemPrompt = { "" }
            filterOutput {
                +NumberFilter::class
            }
        }
        val (_, output) = executeAgent(agent as ChatAgent, "hello", "1234")
        assertThat(output.transcript.last().content).isEqualTo("NUMBER")
    }
}
