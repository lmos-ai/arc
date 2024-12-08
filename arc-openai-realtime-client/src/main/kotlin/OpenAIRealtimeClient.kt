// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.agent.client.ws

import ai.ancf.lmos.arc.agents.ArcException
import ai.ancf.lmos.arc.agents.conversation.AssistantMessage
import ai.ancf.lmos.arc.agents.conversation.BinaryData
import ai.ancf.lmos.arc.agents.conversation.ConversationMessage
import ai.ancf.lmos.arc.agents.conversation.DataStream
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

/**
 * Implementation of [ChatCompleter] that uses OpenAI's Realtime API to communicate with the agents.
 */
class OpenAIRealtimeClient(private val url: String, private val key: String) : Closeable, ChatCompleter {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val closing = AtomicBoolean(false)
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

    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun complete(
        messages: List<ConversationMessage>,
        functions: List<LLMFunction>?,
        settings: ChatCompletionSettings?,
    ) = result<AssistantMessage, ArcException> {
        val response = AtomicReference("")
        val audioData = AtomicReference(emptyList<ByteArray>())

        client.webSocket(url, {
            header("Authorization", "Bearer $key")
            header("OpenAI-Beta", "realtime=v1")
        }) {
            log.debug("Connected to $url")

            send(ConversationItemCreate(messages.last().toConversationItem()!!))
            send(ResponseCreate())

            withTimeoutOrNull(20_000) {
                var done = false
                while (!done) {
                    val data = incoming.receive().data.decodeToString()
                    val dataJson = json.parseToJsonElement(data)
                    val type = dataJson.jsonObject["type"]?.jsonPrimitive?.content
                    log.debug("Received $type $data")
                    //  println("Received $type $data")

                    when (type) {
                        "response.audio.delta" -> {
                            val dataText = dataJson.jsonObject["delta"]!!.jsonPrimitive.content
                            audioData.set(audioData.get() + Base64.decode(dataText))
                        }

                        "response.audio_transcript.done" -> {
                            val dataText = json.decodeFromJsonElement<AudioTranscriptDoneEvent>(dataJson)
                            println("Response done: ${dataText}")
                            response.set(dataText.transcript)
                        }

                        "response.done" -> {
                            done = true
                        }
                    }
                }
            }
            close()
        }
        AssistantMessage(
            response.get(),
            binaryData = listOf(BinaryData(mimeType = "audio/wav", stream = ArrayDataStream(data = audioData.get())))
        )
    }

    @OptIn(ExperimentalEncodingApi::class)
    private suspend fun ConversationMessage.toConversationItem() = when (this) {
        is UserMessage -> Item(
            type = "message",
            role = "user",
            content = listOf(
                //Content(
                //    text = content, type = "input_text"
                //),
                Content(
                    text = Base64.encode(binaryData.last().readAllBytes()), type = "input_audio"
                 )
            )
        )

        is AssistantMessage -> Item(
            type = "message",
            role = "assistant",
            content = listOf(
                Content(text = content, type = "text_input")
            )
        )

        else -> null
    }

    private suspend inline fun <reified T> WebSocketSession.send(msg: T) {
        send(Frame.Text(json.encodeToString(msg)))
    }

    private suspend fun DefaultClientWebSocketSession.nextMessage(): String {
        val response = incoming.receive() as Frame.Text
        return response.readText().let {
            log.trace("Received $it")
            it
        }
    }

    override fun close() {
        closing.set(true)
        client.close()
    }
}


class ArrayDataStream(private val data: List<ByteArray>) : DataStream {

    override fun stream(): Flow<ByteArray> = flow { data.forEach { emit(it) } }
}

