// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.client.ollama

import io.github.lmos.arc.agents.ArcException
import io.github.lmos.arc.agents.FeatureNotSupportedException
import io.github.lmos.arc.agents.conversation.AssistantMessage
import io.github.lmos.arc.agents.conversation.ConversationMessage
import io.github.lmos.arc.agents.conversation.SystemMessage
import io.github.lmos.arc.agents.conversation.UserMessage
import io.github.lmos.arc.agents.events.EventPublisher
import io.github.lmos.arc.agents.functions.LLMFunction
import io.github.lmos.arc.agents.llm.ChatCompleter
import io.github.lmos.arc.agents.llm.ChatCompletionSettings
import io.github.lmos.arc.agents.llm.LLMFinishedEvent
import io.github.lmos.arc.agents.llm.LLMStartedEvent
import io.github.lmos.arc.agents.llm.OutputFormat.JSON
import io.github.lmos.arc.core.Result
import io.github.lmos.arc.core.ensure
import io.github.lmos.arc.core.failWith
import io.github.lmos.arc.core.finally
import io.github.lmos.arc.core.result
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import kotlin.time.Duration
import kotlin.time.measureTime

/**
 * Calls a Ollama Chat endpoint to complete a conversation.
 */
class OllamaClient(
    private val languageModel: OllamaClientConfig,
    private val eventHandler: EventPublisher? = null,
) : ChatCompleter {

    private val log = LoggerFactory.getLogger(javaClass)

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
    }

    private val client = HttpClient(CIO) {
        install(DefaultRequest) {
            url(languageModel.url)
        }
        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    log.debug(message)
                }
            }
            level = LogLevel.ALL
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 120_000
        }
        install(ContentNegotiation) {
            json(json)
        }
    }

    override suspend fun complete(
        messages: List<ConversationMessage>,
        functions: List<LLMFunction>?,
        settings: ChatCompletionSettings?,
    ) = result<AssistantMessage, ArcException> {
        val ollamaMessages = toOllamaMessages(messages)

        ensure(functions.isNullOrEmpty()) {
            FeatureNotSupportedException("OllamaClient currently does not support functions!")
        }

        eventHandler?.publish(LLMStartedEvent(languageModel.modelName))
        val result: Result<ChatResponse, ArcException>
        val duration = measureTime {
            result = chat(ollamaMessages, settings)
        }
        var chatCompletions: ChatResponse? = null
        finally { publishEvent(it, chatCompletions, duration) }
        chatCompletions = result failWith { it }
        AssistantMessage(chatCompletions.message.content)
    }

    private fun publishEvent(
        result: Result<AssistantMessage, ArcException>,
        chatCompletions: ChatResponse?,
        duration: Duration,
    ) {
        eventHandler?.publish(
            LLMFinishedEvent(
                result,
                languageModel.modelName,
                chatCompletions?.let { it.promptTokenCount + it.responseTokenCount } ?: -1,
                chatCompletions?.promptTokenCount ?: -1,
                chatCompletions?.responseTokenCount ?: -1,
                0,
                duration,
            ),
        )
    }

    private suspend fun chat(messages: List<ChatMessage>, settings: ChatCompletionSettings?) =
        result<ChatResponse, ArcException> {
            val response: HttpResponse = client.post("${languageModel.url}/api/chat") {
                contentType(ContentType.Application.Json)
                setBody(
                    ChatRequest(
                        languageModel.modelName,
                        messages,
                        stream = false,
                        format = if (settings?.format == JSON) "json" else null,
                    ),
                )
            }.body()
            ensure(response.status.isSuccess()) { ArcException("Failed to complete chat: ${response.status}!") }
            json.decodeFromString(response.bodyAsText()) // server is sending wrong content type
            // so that ktor does not decode it correctly.
        }

    private fun toOllamaMessages(messages: List<ConversationMessage>) =
        messages.map {
            when (it) {
                is UserMessage -> if (it.binaryData.isNotEmpty()) {
                    ChatMessage(
                        content = it.content,
                        role = "user",
                        images = it.binaryData.map { it.data.encodeBase64() },
                    )
                } else {
                    ChatMessage(content = it.content, role = "user")
                }

                is AssistantMessage -> ChatMessage(content = it.content, role = "assistant")
                is SystemMessage -> ChatMessage(content = it.content, role = "system")
            }
        }
}

@Serializable
data class ChatRequest(
    val model: String,
    val messages: List<ChatMessage>,
    val format: String?,
    val stream: Boolean = false,
)

@Serializable
data class ChatMessage(
    val role: String,
    val content: String,
    val images: List<String>? = null,
)

@Serializable
data class ChatResponse(
    @SerialName("prompt_eval_count")
    val promptTokenCount: Int = -1,
    @SerialName("eval_count")
    val responseTokenCount: Int,
    val message: ChatMessage,
)
