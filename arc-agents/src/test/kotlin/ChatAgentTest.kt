// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.agents

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
}
