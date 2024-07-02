// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0
package io.github.lmos.arc.spring.ai

import io.github.lmos.arc.agents.ArcException
import io.github.lmos.arc.agents.conversation.AssistantMessage
import io.github.lmos.arc.agents.conversation.ConversationMessage
import io.github.lmos.arc.agents.events.EventPublisher
import io.github.lmos.arc.agents.functions.LLMFunction
import io.github.lmos.arc.agents.llm.ChatCompleter
import io.github.lmos.arc.agents.llm.ChatCompletionSettings
import io.github.lmos.arc.agents.llm.LLMFinishedEvent
import io.github.lmos.arc.agents.llm.LLMStartedEvent
import io.github.lmos.arc.core.Result
import io.github.lmos.arc.core.failWith
import io.github.lmos.arc.core.mapFailure
import io.github.lmos.arc.core.result
import org.springframework.ai.chat.model.ChatModel
import org.springframework.ai.chat.model.ChatResponse
import org.springframework.ai.chat.prompt.ChatOptionsBuilder
import org.springframework.ai.chat.prompt.Prompt
import kotlin.time.Duration
import kotlin.time.measureTime

/**
 * Implements the ChatCompleter interface using the Spring AI ChatModel.
 */
open class SpringChatClient(
    protected val client: ChatModel,
    private val modelName: String,
    private val eventHandler: EventPublisher? = null,
) : ChatCompleter {

    /**
     * Calls the LLM.
     */
    protected open fun call(
        messages: List<ConversationMessage>,
        functions: List<LLMFunction>?,
        settings: ChatCompletionSettings?,
    ): Result<ChatResponse, Exception> = result<ChatResponse, Exception> {
        val prompt = Prompt(messages.toSpringAI(), settings.toSpringAI())
        client.call(prompt)
    }

    override suspend fun complete(
        messages: List<ConversationMessage>,
        functions: List<LLMFunction>?,
        settings: ChatCompletionSettings?,
    ) = result<AssistantMessage, ArcException> {
        eventHandler?.publish(LLMStartedEvent(modelName))

        var chatResponse: ChatResponse? = null
        val result: Result<AssistantMessage, ArcException>
        val duration = measureTime {
            result = result<AssistantMessage, Exception> {
                chatResponse = call(messages, functions, settings) failWith { it }
                AssistantMessage(chatResponse!!.result.output.content)
            }.mapFailure { ArcException(it.message ?: "Unknown exception!", it) }
        }

        publishEvent(result, messages, functions, chatResponse, duration, settings)
        result failWith { it }
    }

    /**
     * Converts ChatCompletionSettings to Spring AI ChatOptions.
     */
    private fun ChatCompletionSettings?.toSpringAI() = ChatOptionsBuilder.builder().apply {
        this@toSpringAI?.temperature?.let { withTemperature(it.toFloat()) }
        this@toSpringAI?.topP?.let { withTopP(it.toFloat()) }
        this@toSpringAI?.topK?.let { withTopK(it) }
    }.build()

    /**
     * Publishes the LLMFinishedEvent.
     */
    private fun publishEvent(
        result: Result<AssistantMessage, ArcException>,
        messages: List<ConversationMessage>,
        functions: List<LLMFunction>?,
        chatResponse: ChatResponse?,
        duration: Duration,
        settings: ChatCompletionSettings?,
    ) {
        eventHandler?.publish(
            LLMFinishedEvent(
                result,
                messages,
                functions,
                modelName,
                chatResponse?.metadata?.usage?.totalTokens?.toInt() ?: -1,
                chatResponse?.metadata?.usage?.promptTokens?.toInt() ?: -1,
                chatResponse?.metadata?.usage?.generationTokens?.toInt() ?: -1,
                0,
                duration,
                settings = settings,
            ),
        )
    }
}
