// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.agent.client

import ai.ancf.lmos.arc.agent.client.ws.OpenAIRealtimeClient
import ai.ancf.lmos.arc.agents.conversation.UserMessage
import ai.ancf.lmos.arc.core.getOrThrow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

val key =
    "sk-proj--"


class AgentClientTest {

    @Test
    fun `test`(): Unit = runBlocking {
        OpenAIRealtimeClient(
            "wss://api.openai.com/v1/realtime?model=gpt-4o-realtime-preview-2024-10-01",
            key
        ).use { client ->
            val result = client.complete(
                listOf(
                    UserMessage("Hello, do you know what time it is?")
                )
            ).getOrThrow()
            println("-------> $result")
            val out = File("output.wav")
            result.binaryData.first().stream?.stream()?.toList()?.reduce { acc, bytes -> acc + bytes }?.let {
                pcm16ToWav(it, 24000, 1, out)
            }
        }
    }

    fun pcm16ToWav(pcmData: ByteArray, sampleRate: Int, channels: Int, outputFile: File) {
        val byteRate = sampleRate * channels * 2 // Bytes per second
        val blockAlign = channels * 2 // Bytes per sample
        val chunkSize = 36 + pcmData.size // Chunk size, including header

        // Create header data
        val header = ByteBuffer.allocate(44)
        header.order(ByteOrder.LITTLE_ENDIAN)
        header.put("RIFF".toByteArray())
        header.putInt(chunkSize)
        header.put("WAVE".toByteArray())
        header.put("fmt ".toByteArray())
        header.putInt(16) // Format chunk size
        header.putShort(1.toShort()) // Audio format (1 = PCM)
        header.putShort(channels.toShort())
        header.putInt(sampleRate)
        header.putInt(byteRate)
        header.putShort(blockAlign.toShort())
        header.putShort(16.toShort()) // Bits per sample
        header.put("data".toByteArray())
        header.putInt(pcmData.size)

        // Write header and data to file
        FileOutputStream(outputFile).use { fos ->
            fos.write(header.array())
            fos.write(pcmData)
        }
    }

}
