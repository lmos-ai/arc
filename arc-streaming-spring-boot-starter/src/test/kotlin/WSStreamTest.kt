// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package org.eclipse.lmos.arc.ws

import org.eclipse.lmos.arc.agent.client.stream.DataStream
import org.eclipse.lmos.arc.agent.client.stream.StreamClient
import org.eclipse.lmos.arc.api.BinaryData
import org.eclipse.lmos.arc.api.STREAM_SOURCE
import org.eclipse.lmos.arc.api.agentRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class WSStreamTest {

    @Test
    fun `test endpoint receives data`(): Unit = runBlocking {
        lastInputMessage.set(null)

        val dataProvider = DataStream()
        StreamClient().callAgent(
            agentRequest("Hello", "1", BinaryData("audio/pcm", source = STREAM_SOURCE)),
            url = "ws://localhost:8080/ws/agent",
            dataStream = dataProvider,
        )
        dataProvider.send("This is data".encodeToByteArray())

        assertThat(String(lastInputMessage.get().binaryData.first().readAllBytes())).isEqualTo("This is data")
    }

    @Test
    fun `server test`(): Unit = runBlocking {
        delay(9000_000)
    }
}
