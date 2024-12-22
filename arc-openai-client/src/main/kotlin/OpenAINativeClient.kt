// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

import ai.ancf.lmos.arc.agents.ArcException
import ai.ancf.lmos.arc.agents.conversation.*
import ai.ancf.lmos.arc.agents.events.EventPublisher
import ai.ancf.lmos.arc.agents.functions.LLMFunction
import ai.ancf.lmos.arc.agents.functions.ParameterSchema
import ai.ancf.lmos.arc.agents.functions.ParameterType
import ai.ancf.lmos.arc.agents.functions.ParametersSchema
import ai.ancf.lmos.arc.agents.llm.*
import ai.ancf.lmos.arc.agents.llm.OutputFormat.JSON
import ai.ancf.lmos.arc.core.*
import com.openai.client.OpenAIClient
import com.openai.client.OpenAIClientAsync
import com.openai.core.JsonValue
import com.openai.models.*
import kotlinx.coroutines.future.await
import kotlinx.coroutines.reactive.awaitFirst
import org.slf4j.LoggerFactory
import kotlin.time.Duration
import kotlin.time.measureTime

/**
 * Calls the OpenAI endpoints and automatically handles LLM function calls.
 */
class OpenAINativeClient(
    private val config: OpenAINativeClientConfig,
    private val client: OpenAIClientAsync,
    private val eventHandler: EventPublisher? = null,
) : ChatCompleter,
    TextEmbedder {

    private val log = LoggerFactory.getLogger(javaClass)

    override suspend fun complete(
        messages: List<ConversationMessage>,
        functions: List<LLMFunction>?,
        settings: ChatCompletionSettings?,
    ) =
        result<AssistantMessage, ArcException> {
            val openAIMessages = toOpenAIMessages(messages)
            val openAIFunctions = if (functions != null) toOpenAIFunctions(functions) else null
            val functionCallHandler = FunctionCallHandler(functions ?: emptyList(), eventHandler)

            eventHandler?.publish(LLMStartedEvent(config.modelName))

            val result: Result<ChatCompletion, ArcException>
            val duration = measureTime {
                result = getChatCompletions(openAIMessages, openAIFunctions, functionCallHandler, settings)
            }

            var chatCompletions: ChatCompletions? = null
            finally { publishEvent(it, messages, functions, chatCompletions, duration, functionCallHandler, settings) }
            chatCompletions = result failWith { it }
            chatCompletions.getFirstAssistantMessage(
                sensitive = functionCallHandler.calledSensitiveFunction(),
                settings = settings,
            )
        }

    private fun publishEvent(
        result: Result<AssistantMessage, ArcException>,
        messages: List<ConversationMessage>,
        functions: List<LLMFunction>?,
        chatCompletions: ChatCompletions?,
        duration: Duration,
        functionCallHandler: FunctionCallHandler,
        settings: ChatCompletionSettings?,
    ) {
        eventHandler?.publish(
            LLMFinishedEvent(
                result,
                messages,
                functions,
                config.modelName,
                chatCompletions?.usage?.totalTokens ?: -1,
                chatCompletions?.usage?.promptTokens ?: -1,
                chatCompletions?.usage?.completionTokens ?: -1,
                functionCallHandler.calledFunctions.size,
                duration,
                settings = settings,
            ),
        )
    }

    private fun ChatCompletions.getFirstAssistantMessage(
        sensitive: Boolean = false,
        settings: ChatCompletionSettings?,
    ) = choices.first().message.content.let {
        AssistantMessage(
            it,
            sensitive = sensitive,
            format = when (settings?.format) {
                JSON -> MessageFormat.JSON
                else -> MessageFormat.TEXT
            },
        )
    }

    private suspend fun getChatCompletions(
        messages: List<ChatCompletionMessageParam>,
        openAIFunctions: List<ChatCompletionCreateParams.Function>? = null,
        functionCallHandler: FunctionCallHandler,
        settings: ChatCompletionSettings?,
    ): Result<ChatCompletion, ArcException> {
        val params = ChatCompletionCreateParams.builder()
            .model(config.modelName)
            .messages(messages)
            .functions(openAIFunctions?: listOf())
            .apply {
                settings?.temperature?.let { temperature(it) }
                settings?.topP?.let { topP(it) }
                settings?.seed?.let { seed(it) }
                settings?.n?.let { n(it.toLong()) }
                settings?.maxTokens?.let { maxTokens(it.toLong()) }
                settings?.format?.takeIf { JSON == it }?.let {
                    ChatCompletionCreateParams.ResponseFormat.ofResponseFormatJsonObject(
                        ResponseFormatJsonObject.builder().type(
                            ResponseFormatJsonObject.Type.JSON_OBJECT
                        ).build()
                    )
                }
            }.build()

        val chatCompletions = try {
            client.chat().completions().create(params).await()
        } catch (ex: Exception) {
            log.error("Call to OpenAI failed!", ex)
            return Failure(mapOpenAIException(ex))
        }

        log.debug("ChatCompletions: ${chatCompletions.choices()[0].finishReason()} (${chatCompletions.choices().size})")

        val newMessages = functionCallHandler.handle(chatCompletions).getOrThrow()
        if (newMessages.isNotEmpty()) {
            return getChatCompletions(messages + newMessages, openAIFunctions, functionCallHandler, settings)
        }
        return Success(chatCompletions)
    }

    private fun mapOpenAIException(ex: Exception): ArcException = when (ex) {
        is ClientAuthenticationException -> ArcException(ex.message ?: "Unexpected error!", ex)
        else -> ArcException(ex.message ?: "Unexpected error!", ex)
    }

    private fun toOpenAIMessages(messages: List<ConversationMessage>) = messages.map { msg ->
        when (msg) {
            is UserMessage -> ChatCompletionMessageParam.ofChatCompletionUserMessageParam(
                ChatCompletionUserMessageParam.builder()
                    .content(ChatCompletionUserMessageParam.Content.ofTextContent(msg.content)).build()
            )

            is SystemMessage -> ChatCompletionMessageParam.ofChatCompletionSystemMessageParam(
                ChatCompletionSystemMessageParam.builder()
                    .content(ChatCompletionSystemMessageParam.Content.ofTextContent(msg.content)).build()
            )

            is AssistantMessage -> ChatCompletionMessageParam.ofChatCompletionAssistantMessageParam(
                ChatCompletionAssistantMessageParam.builder()
                    .content(ChatCompletionAssistantMessageParam.Content.ofTextContent(msg.content)).build()
            )

            is DeveloperMessage -> ChatCompletionMessageParam.ofChatCompletionDeveloperMessageParam(
                ChatCompletionDeveloperMessageParam.builder()
                    .content(ChatCompletionDeveloperMessageParam.Content.ofTextContent(msg.content)).build()
            )
        }
    }

    /**
     * Converts functions to openai functions.
     */
    private fun toOpenAIFunctions(functions: List<LLMFunction>) = functions.map { fn ->
        val map = fn.parameters.parameters.associate { param ->
            param.name to mapOf(
                "type" to param.type.schemaType,
                "description" to param.description,
                "enum" to param.enum
            )
        }
        ChatCompletionCreateParams.Function.builder().name(fn.name)
            .description(fn.description)
            .parameters(
                FunctionParameters.builder().putAdditionalProperty("type", JsonValue.from("object"))
                    .putAdditionalProperty("properties", JsonValue.from(map))
                    .putAdditionalProperty("required", JsonValue.from(fn.parameters.required)).build()
            )
            .build()
    }.takeIf { it.isNotEmpty() }
}

    override suspend fun embed(texts: List<String>) = result<TextEmbeddings, Exception> {
        val embedding = client.getEmbeddings(config.modelName, EmbeddingsOptions(texts)).awaitFirst().let { result ->
            result.data.map { e -> TextEmbedding(texts[e.promptIndex], e.embedding) }
        }
        TextEmbeddings(embedding)
    }.mapFailure { ArcException("Failed to create text embeddings!", it) }
}
