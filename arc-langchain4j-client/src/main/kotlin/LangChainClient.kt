// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.client.langchain4j

import ai.ancf.lmos.arc.agents.ArcException
import ai.ancf.lmos.arc.agents.conversation.AssistantMessage
import ai.ancf.lmos.arc.agents.conversation.BinaryData
import ai.ancf.lmos.arc.agents.conversation.ConversationMessage
import ai.ancf.lmos.arc.agents.conversation.SystemMessage
import ai.ancf.lmos.arc.agents.conversation.UserMessage
import ai.ancf.lmos.arc.agents.events.EventPublisher
import ai.ancf.lmos.arc.agents.functions.LLMFunction
import ai.ancf.lmos.arc.agents.functions.toSchemaMap
import ai.ancf.lmos.arc.agents.llm.ChatCompleter
import ai.ancf.lmos.arc.agents.llm.ChatCompletionSettings
import ai.ancf.lmos.arc.agents.llm.LLMFinishedEvent
import ai.ancf.lmos.arc.agents.llm.LLMStartedEvent
import ai.ancf.lmos.arc.core.Failure
import ai.ancf.lmos.arc.core.Result
import ai.ancf.lmos.arc.core.Success
import ai.ancf.lmos.arc.core.failWith
import ai.ancf.lmos.arc.core.finally
import ai.ancf.lmos.arc.core.getOrThrow
import ai.ancf.lmos.arc.core.result
import dev.langchain4j.agent.tool.ToolParameters
import dev.langchain4j.agent.tool.ToolSpecification
import dev.langchain4j.data.message.AiMessage
import dev.langchain4j.data.message.AudioContent
import dev.langchain4j.data.message.ChatMessage
import dev.langchain4j.data.message.Content
import dev.langchain4j.data.message.ImageContent
import dev.langchain4j.data.message.TextContent
import dev.langchain4j.data.message.VideoContent
import dev.langchain4j.model.chat.ChatLanguageModel
import dev.langchain4j.model.output.Response
import org.slf4j.LoggerFactory
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.Duration
import kotlin.time.measureTime

/**
 * Wraps a LangChain4j ChatLanguageModel to provide a ChatCompleter interface.
 */
class LangChainClient(
    private val languageModel: LangChainConfig,
    private val clientBuilder: (LangChainConfig, ChatCompletionSettings?) -> ChatLanguageModel,
    private val eventHandler: EventPublisher? = null,
) : ChatCompleter {

    private val log = LoggerFactory.getLogger(javaClass)

    override suspend fun complete(
        messages: List<ConversationMessage>,
        functions: List<LLMFunction>?,
        settings: ChatCompletionSettings?,
    ) = result<AssistantMessage, ArcException> {
        val langChainMessages = toLangChainMessages(messages)
        val langChainFunctions = if (functions != null) toLangChainFunctions(functions) else null
        val functionCallHandler = FunctionCallHandler(functions ?: emptyList(), eventHandler)

        eventHandler?.publish(LLMStartedEvent(languageModel.modelName))

        val result: Result<Response<AiMessage>, ArcException>
        val duration = measureTime {
            result = chat(langChainMessages, langChainFunctions, settings, functionCallHandler)
        }

        var response: Response<AiMessage>? = null
        finally { publishEvent(it, messages, functions, response, duration, settings, functionCallHandler) }
        response = result failWith { ArcException("Failed to call LLM!", it) }
        AssistantMessage(response.content().text(), sensitive = false)
    }

    private fun publishEvent(
        result: Result<AssistantMessage, ArcException>,
        messages: List<ConversationMessage>,
        functions: List<LLMFunction>?,
        response: Response<AiMessage>?,
        duration: Duration,
        settings: ChatCompletionSettings?,
        functionCallHandler: FunctionCallHandler,
    ) {
        eventHandler?.publish(
            LLMFinishedEvent(
                result,
                messages,
                functions,
                languageModel.modelName,
                totalTokens = response?.tokenUsage()?.totalTokenCount() ?: -1,
                promptTokens = response?.tokenUsage()?.inputTokenCount() ?: -1,
                completionTokens = response?.tokenUsage()?.outputTokenCount() ?: -1,
                functionCallHandler.calledFunctions.size,
                duration,
                settings = settings,
            ),
        )
    }

    private suspend fun chat(
        messages: List<ChatMessage>,
        langChainFunctions: List<ToolSpecification>? = null,
        settings: ChatCompletionSettings?,
        functionCallHandler: FunctionCallHandler,
    ): Result<Response<AiMessage>, ArcException> {
        return try {
            val client = clientBuilder(languageModel, settings)
            val response = if (langChainFunctions?.isNotEmpty() == true) {
                client.generate(messages, langChainFunctions)
            } else {
                client.generate(messages)
            }

            log.debug("ChatCompletions: ${response.finishReason()} (${response.content().toolExecutionRequests()})")

            val newMessages = functionCallHandler.handle(response.content()).getOrThrow()
            return if (newMessages.isNotEmpty()) {
                chat(messages + newMessages, langChainFunctions, settings, functionCallHandler)
            } else {
                Success(response)
            }
        } catch (e: Exception) {
            Failure(ArcException("Failed to call LLM!", e))
        }
    }

    private suspend fun toLangChainMessages(messages: List<ConversationMessage>) =
        messages.map {
            when (it) {
                is UserMessage -> {
                    if (it.binaryData.isNotEmpty()) {
                        dev.langchain4j.data.message.UserMessage.from(
                            listOf(TextContent.from(it.content)) +
                                it.binaryData.map { data -> data.toContent() },
                        )
                    } else {
                        dev.langchain4j.data.message.UserMessage(it.content)
                    }
                }

                is AssistantMessage -> dev.langchain4j.data.message.AiMessage(it.content)
                is SystemMessage -> dev.langchain4j.data.message.SystemMessage(it.content)
            }
        }

    @OptIn(ExperimentalEncodingApi::class)
    private suspend fun BinaryData.toContent(): Content {
        val bytes = readAllBytes()
        log.debug("Converting binary data to content: $mimeType with ${bytes.size} bytes")
        return when {
            mimeType.startsWith("audio/") -> AudioContent.from(Base64.encode(bytes), mimeType)
            mimeType.startsWith("video/") -> VideoContent.from(Base64.encode(bytes), mimeType)
            mimeType.startsWith("image/") -> ImageContent.from(Base64.encode(bytes), mimeType)
            else -> error("Unsupported binary data type: $mimeType!")
        }
    }

    /**
     * Converts functions to openai functions.
     */
    private fun toLangChainFunctions(functions: List<LLMFunction>) = functions.map { fn ->
        ToolSpecification.builder()
            .name(fn.name)
            .description(fn.description)
            .parameters(
                ToolParameters.builder()
                    .apply {
                        properties(fn.parameters.parameters.toSchemaMap())
                        required(fn.parameters.required)
                    }
                    .build(),
            )
            .build()
    }.takeIf { it.isNotEmpty() }
}
