// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.agent.client

import ai.ancf.lmos.arc.agent.client.graphql.GraphQlAgentClient
import ai.ancf.lmos.arc.api.AgentRequest
import ai.ancf.lmos.arc.api.ConversationContext
import ai.ancf.lmos.arc.api.UserContext
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class AgentClientTest {

    @Test
    fun `test client with agent name`(): Unit = runBlocking {
        GraphQlAgentClient("ws://localhost:8083/subscriptions").use { client ->
            val result = client.callAgent(request(), agentName = "Agent01")
            val msg = result.toCollection(mutableListOf()).first().messages.first().content
            assertThat(msg).isEqualTo("1")
        }
    }

    @Test
    fun `test client with no agent name`(): Unit = runBlocking {
        GraphQlAgentClient("ws://localhost:8083/subscriptions").use { client ->
            val result = client.callAgent(request())
            val msg = result.toCollection(mutableListOf()).first().messages.first().content
            assertThat(msg).isEqualTo("2")
        }
    }

    private fun request() = AgentRequest(
        messages = emptyList(),
        systemContext = emptyList(),
        conversationContext = ConversationContext("1"),
        userContext = UserContext("userId", profile = emptyList()),
    )
}
