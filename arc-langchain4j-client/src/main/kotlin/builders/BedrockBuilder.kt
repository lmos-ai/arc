// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.client.langchain4j.bedrock

import ai.ancf.lmos.arc.agents.llm.ChatCompletionSettings
import ai.ancf.lmos.arc.client.langchain4j.LangChainConfig
import dev.langchain4j.model.bedrock.BedrockAnthropicMessageChatModel
import dev.langchain4j.model.chat.ChatLanguageModel
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import java.util.concurrent.ConcurrentHashMap

private val cache = ConcurrentHashMap<Pair<LangChainConfig, ChatCompletionSettings?>, ChatLanguageModel>()

/**
 * Builds a BedrockAnthropicMessageChatModel for the given LangChainConfig and ChatCompletionSettings.
 */
fun bedrockBuilder(
    awsCredentialsProvider: AwsCredentialsProvider? = null,
): (LangChainConfig, ChatCompletionSettings?) -> ChatLanguageModel {
    return { model, settings ->
        cache.computeIfAbsent(model to settings) {
            BedrockAnthropicMessageChatModel.builder()
                .credentialsProvider(
                    awsCredentialsProvider ?: StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(model.accessKeyId, model.secretAccessKey),
                    ),
                )
                .model(model.modelName)
                .apply {
                    if (settings != null) {
                        settings.topP?.let { topP(it.toFloat()) }
                        settings.temperature?.let { temperature(it) }
                        settings.maxTokens?.let { maxTokens(it) }
                        settings.topK?.let { topK(it) }
                    }
                }
                .build()
        }
    }
}
