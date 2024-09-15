// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.agents

import ai.ancf.lmos.arc.agents.conversation.toConversation
import ai.ancf.lmos.arc.agents.dsl.extensions.breakWith
import ai.ancf.lmos.arc.core.getOrThrow
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ChatAgentTest : TestBase() {

    @Test
    fun `test conversation is available in context`(): Unit = runBlocking {
        val agent = agent {
            name = "agent"
            description = "agent description"
            systemPrompt = { "does stuff" }
            filterInput {
                assertThat(input).isNotNull
            }
        } as ChatAgent
        val (_, result) = executeAgent(agent, "question?")
        assertThat(result.transcript.last().content).isEqualTo("answer")
    }

    @Test
    fun `test interrupting agent`(): Unit = runBlocking {
        val agent = agent {
            name = "agent"
            description = "agent description"
            systemPrompt = { "does stuff" }
            filterInput {
                breakWith("interrupted")
            }
        } as ChatAgent
        val result = agent.execute("test".toConversation(User("user")), contextBeans).getOrThrow()
        assertThat(result.transcript.last().content).isEqualTo("interrupted")
    }

    @Test
    fun `test setting message in OutputFilter`(): Unit = runBlocking {
        val agent = agent {
            name = "agent"
            description = "agent description"
            systemPrompt = { "does stuff" }
            filterOutput {
                message = "filterOutput"
            }
        } as ChatAgent
        val (_, result) = executeAgent(agent, "question?")
        assertThat(result.transcript.last().content).isEqualTo("filterOutput")
    }

    @Test
    fun `test setting message in InputFilter`(): Unit = runBlocking {
        val agent = agent {
            name = "agent"
            description = "agent description"
            systemPrompt = { "does stuff" }
            filterInput {
                message = "filterInput"
            }
        } as ChatAgent
        val (input, result) = executeAgent(agent, "question?")
        assertThat(result.transcript.last().content).isEqualTo("answer")
        assertThat(input.last().content).isEqualTo("filterInput")
    }
}
