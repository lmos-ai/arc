// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.agent.client.ws

import ai.ancf.lmos.arc.agents.ArcException
import ai.ancf.lmos.arc.agents.conversation.AssistantMessage
import ai.ancf.lmos.arc.agents.conversation.BinaryData
import ai.ancf.lmos.arc.agents.conversation.ConversationMessage
import ai.ancf.lmos.arc.agents.conversation.DataStream
import ai.ancf.lmos.arc.agents.conversation.SystemMessage
import ai.ancf.lmos.arc.agents.conversation.UserMessage
import ai.ancf.lmos.arc.agents.functions.LLMFunction
import ai.ancf.lmos.arc.agents.llm.ChatCompleter
import ai.ancf.lmos.arc.agents.llm.ChatCompletionSettings
import ai.ancf.lmos.arc.core.result
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.util.concurrent.atomic.AtomicReference
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Implementation of [ChatCompleter] that uses OpenAI's Realtime API to communicate with the agents.
 */
class OpenAIRealtimeClient(private val url: String, private val key: String) : Closeable, ChatCompleter {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        explicitNulls = false
    }

    private val client = HttpClient(CIO) {
        install(WebSockets) {
            pingInterval = 20_000
        }
    }

    override suspend fun complete(
        messages: List<ConversationMessage>,
        functions: List<LLMFunction>?,
        settings: ChatCompletionSettings?,
    ) = result<AssistantMessage, ArcException> {
        val result = AtomicReference<AssistantMessage>()
        client.webSocket(url, {
            header("Authorization", "Bearer $key")
            header("OpenAI-Beta", "realtime=v1")
        }) {
            log.debug("Connected to $url")
            val (text, audio) = consumeEvents(messages, functions, settings)
            result.set(
                AssistantMessage(
                    text,
                    binaryData = listOf(
                        BinaryData(
                            mimeType = "audio/pcm",
                            stream = ArrayDataStream(data = audio),
                        ),
                    ),
                ),
            )
            close()
        }
        result.get()
    }

    @OptIn(ExperimentalEncodingApi::class)
    suspend fun DefaultClientWebSocketSession.consumeEvents(
        messages: List<ConversationMessage>,
        functions: List<LLMFunction>?,
        settings: ChatCompletionSettings?,
    ): Pair<String, List<ByteArray>> {
        val response = AtomicReference("")
        val audioData = AtomicReference(emptyList<ByteArray>())

        incoming.consumeEach { data ->
            val dataText = (data as Frame.Text).readText()
            val dataJson = json.parseToJsonElement(dataText)
            val type = dataJson.jsonObject["type"]?.jsonPrimitive?.content
            log.debug("Received event with type: $type")

            when (type) {
                "session.created" -> {
                    send(
                        SessionUpdate(
                            Session(
                                temperature = settings?.temperature,
                                inputAudioTranscription = InputAudioTranscription(),
                                instructions = messages.firstOrNull { it is SystemMessage }?.content,
                            ),
                        ),
                    )
                }

                "session.updated" -> {
                    log.debug("Session updated: $dataText")
                    sendBinaries(messages)
                    send(InputAudioBufferCommit())
                    send(ResponseCreate())
                }

                "response.audio.delta" -> {
                    val event = json.decodeFromJsonElement<ResponseAudioDelta>(dataJson)
                    audioData.set(audioData.get() + Base64.decode(event.delta))
                }

                "response.audio_transcript.done" -> {
                    val event = json.decodeFromJsonElement<AudioTranscriptDone>(dataJson)
                    response.set(event.transcript)
                }

                "conversation.item.input_audio_transcription.completed" -> {
                    val event = json.decodeFromJsonElement<InputAudioTranscriptionCompleted>(dataJson)
                    log.info("User Transcript ${event.transcript}") // TODO
                }

                "error" -> {
                    throw ArcException(dataText)
                }

                "response.done" -> {
                    return response.get() to audioData.get()
                }

                else -> {
                    log.debug(dataText)
                }
            }
        }
        return response.get() to audioData.get()
    }

    /**
     * Sends the binary data of the messages to the server.
     */
    @OptIn(ExperimentalEncodingApi::class)
    private suspend fun DefaultClientWebSocketSession.sendBinaries(messages: List<ConversationMessage>) {
        messages.last().binaryData.last().readAllBytes().chunked(8192).forEach { chunk ->
            send(InputAudioBufferAppend(audio = Base64.encode(chunk)))
            log.info("Sent audio chunk ${chunk.size}")
        }
    }

    /**
     * Chunks an array of bytes into smaller chunks.
     */
    private fun ByteArray.chunked(chunkSize: Int): List<ByteArray> {
        if (chunkSize <= 0) {
            throw IllegalArgumentException("Chunk size must be positive")
        }

        val chunks = mutableListOf<ByteArray>()
        var index = 0
        while (index < size) {
            val endIndex = kotlin.math.min(index + chunkSize, size)
            chunks.add(copyOfRange(index, endIndex))
            index += chunkSize
        }
        return chunks
    }

    /**
     * Converts a [ConversationMessage]s to OpenAI Conversation Items.
     */
    private fun ConversationMessage.toConversationItem() = when (this) {
        is UserMessage -> Item(
            type = "message",
            role = "user",
            content = listOf(textContent(content)),
        )

        is AssistantMessage -> Item(
            type = "message",
            role = "assistant",
            content = listOf(textContent(content)),
        )

        else -> null
    }

    private suspend inline fun <reified T> WebSocketSession.send(msg: T) {
        send(Frame.Text(json.encodeToString(msg)))
    }

    /**
     * Closes the client and websocket connection.
     */
    override fun close() {
        client.close()
    }
}

class ArrayDataStream(private val data: List<ByteArray>) : DataStream {

    override fun stream(): Flow<ByteArray> = flow { data.forEach { emit(it) } }
}
