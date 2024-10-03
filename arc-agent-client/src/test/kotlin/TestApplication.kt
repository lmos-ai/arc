// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.agent.client

import ai.ancf.lmos.arc.agents.ArcException
import ai.ancf.lmos.arc.agents.conversation.AssistantMessage
import ai.ancf.lmos.arc.agents.conversation.ConversationMessage
import ai.ancf.lmos.arc.agents.dsl.extensions.breakWith
import ai.ancf.lmos.arc.agents.functions.LLMFunction
import ai.ancf.lmos.arc.agents.llm.ChatCompleter
import ai.ancf.lmos.arc.agents.llm.ChatCompleterProvider
import ai.ancf.lmos.arc.agents.llm.ChatCompletionSettings
import ai.ancf.lmos.arc.core.result
import ai.ancf.lmos.arc.spring.Agents
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
open class TestApplication {

    @Bean
    open fun agent02(agent: Agents) = agent {
        name = "Agent02"
        filterInput { breakWith("2") }
        prompt { "" }
    }

    @Bean
    open fun agent01(agent: Agents) = agent {
        name = "Agent01"
        filterInput { breakWith("1") }
        prompt { "" }
    }

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
}
