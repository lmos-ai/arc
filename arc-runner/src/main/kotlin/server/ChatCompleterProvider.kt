// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0
package ai.ancf.lmos.arc.runner.server

import ai.ancf.lmos.arc.agents.events.EventPublisher
import ai.ancf.lmos.arc.agents.llm.ChatCompleterProvider
import ai.ancf.lmos.arc.client.azure.AzureAIClient
import ai.ancf.lmos.arc.client.azure.AzureClientConfig
import ai.ancf.lmos.arc.client.langchain4j.LangChainClient
import ai.ancf.lmos.arc.client.langchain4j.LangChainConfig
import ai.ancf.lmos.arc.client.langchain4j.builders.bedrockBuilder
import ai.ancf.lmos.arc.client.langchain4j.builders.geminiBuilder
import ai.ancf.lmos.arc.client.langchain4j.builders.groqBuilder
import ai.ancf.lmos.arc.client.langchain4j.builders.ollamaBuilder
import com.azure.ai.openai.OpenAIClientBuilder
import com.azure.core.credential.AzureKeyCredential
import com.azure.core.credential.KeyCredential
import com.azure.identity.DefaultAzureCredentialBuilder

/**
 * Provides a ChatCompleterProvider based on the given configuration.
 */
fun chatCompleterProvider(config: AIClientConfig, eventPublisher: EventPublisher): ChatCompleterProvider {
    val langChainConfig = LangChainConfig(
        modelName = config.modelName,
        url = config.url,
        apiKey = config.apiKey,
        accessKeyId = config.accessKey,
        secretAccessKey = config.accessSecret,
    )

    val aiClient = when {
        "gemini" == config.client -> LangChainClient(langChainConfig, geminiBuilder(), eventPublisher)

        "bedrock" == config.client -> LangChainClient(langChainConfig, bedrockBuilder(), eventPublisher)

        "groq" == config.client -> LangChainClient(langChainConfig, groqBuilder(), eventPublisher)

        "ollama" == config.client -> LangChainClient(langChainConfig, ollamaBuilder(), eventPublisher)

        "openai" == config.client -> AzureAIClient(
            AzureClientConfig(config.modelName, config.url ?: "", config.apiKey ?: ""),
            OpenAIClientBuilder()
                .apply { if (config.url != null) endpoint(config.url) }
                .credential(KeyCredential(config.apiKey))
                .buildAsyncClient(),
            eventPublisher,
        )

        "azure" == config.client && config.apiKey != null
        -> AzureAIClient(
            AzureClientConfig(config.modelName, config.url ?: "", config.apiKey ?: ""),
            OpenAIClientBuilder()
                .endpoint(config.url)
                .credential(AzureKeyCredential(config.apiKey))
                .buildAsyncClient(),
            eventPublisher,
        )

        "azure" == config.client && config.url != null -> AzureAIClient(
            AzureClientConfig(config.modelName, config.url ?: "", config.apiKey ?: ""),
            OpenAIClientBuilder()
                .credential(DefaultAzureCredentialBuilder().build())
                .endpoint(config.url)
                .buildAsyncClient(),
            eventPublisher,
        )

        else -> error("No client could be configured for client: ${config.client}")
    }

    return ChatCompleterProvider { aiClient }
}

data class AIClientConfig(
    val id: String,
    val client: String,
    val modelName: String,
    val url: String? = null,
    val apiKey: String? = null,
    val accessKey: String? = null,
    val accessSecret: String? = null,
)
