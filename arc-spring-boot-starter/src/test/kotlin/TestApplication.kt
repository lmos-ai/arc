// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.spring

import io.github.lmos.arc.agents.ArcException
import io.github.lmos.arc.agents.conversation.AssistantMessage
import io.github.lmos.arc.agents.conversation.ConversationMessage
import io.github.lmos.arc.agents.functions.LLMFunction
import io.github.lmos.arc.agents.llm.ChatCompleter
import io.github.lmos.arc.agents.llm.ChatCompleterProvider
import io.github.lmos.arc.agents.llm.ChatCompletionSettings
import io.github.lmos.arc.core.result
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
open class TestApplication {

    @Bean
    open fun chatCompleterProvider() = ChatCompleterProvider {
        object : ChatCompleter {
            override suspend fun complete(
                messages: List<ConversationMessage>,
                functions: List<LLMFunction>?,
                settings: ChatCompletionSettings?,
            ) = result<AssistantMessage, ArcException> {
                functions?.forEach { function ->
                    function.execute(mapOf("param" to "test"))
                }
                AssistantMessage("Hello!")
            }
        }
    }

    @Bean
    open fun myAgent(agent: Agents) = agent {
        name = "agentBean"
        systemPrompt = { "you are a helpful agent that tell funny jokes." }
        description = "Test"
    }

    @Bean
    open fun myFunction(function: Functions) = function(
        name = "get_weather_bean",
        description = "Returns real-time weather information for any location",
    ) {
        """
        The weather is good in Berlin. It is 20 degrees celsius.
    """
    }
}
