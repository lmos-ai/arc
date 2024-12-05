// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.ws

import ai.ancf.lmos.arc.agent.client.ws.DataProvider
import ai.ancf.lmos.arc.agent.client.ws.WsClient
import ai.ancf.lmos.arc.api.BinaryData
import ai.ancf.lmos.arc.api.agentRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class AudioStreamTest {

    @Test
    fun `test agentSubscription is defined`(): Unit = runBlocking {
        val dataProvider = DataProvider()
        WsClient().callAgent(
            agentRequest(
                content = "Hello",
                conversationId = "1",
                BinaryData("audio/wav", "stream://audio"),
            ),
            agentName = "",
            url = "ws://localhost:8080/ws/agent",
            dataProvider
        )
        dataProvider.send("This is data".encodeToByteArray(), last = true)

        delay(200_000)
    }
}
