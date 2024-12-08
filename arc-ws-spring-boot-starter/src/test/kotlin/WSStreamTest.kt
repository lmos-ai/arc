// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.ws

import ai.ancf.lmos.arc.agent.client.ws.DataStream
import ai.ancf.lmos.arc.agent.client.ws.WsClient
import ai.ancf.lmos.arc.api.BinaryData
import ai.ancf.lmos.arc.api.STREAM_SOURCE
import ai.ancf.lmos.arc.api.agentRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class WSStreamTest {

    @Test
    fun `test endpoint receives data`(): Unit = runBlocking {
        lastBinaryData.set(null)

        val dataProvider = DataStream()
        WsClient().callAgent(
            agentRequest("Hello", "1", BinaryData("audio/wav", source = STREAM_SOURCE)),
            url = "ws://localhost:8080/ws/agent",
            dataStream = dataProvider,
        )
        dataProvider.send("This is data".encodeToByteArray())

        assertThat(lastBinaryData.get()).isEqualTo("This is data")
    }

    @Test
    fun `server test`(): Unit = runBlocking {
       delay(900_000)
    }
}
