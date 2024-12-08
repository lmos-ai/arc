// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.ws

import ai.ancf.lmos.arc.agent.client.ws.OpenAIRealtimeClient
import ai.ancf.lmos.arc.agents.llm.ChatCompleterProvider
import ai.ancf.lmos.arc.spring.Agents
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import java.util.concurrent.atomic.AtomicReference

@SpringBootApplication
open class TestApplication {

    val key = ""
    @Bean
    open fun myAgent(agent: Agents) = agent {
        name = "agent"
        prompt { "you are a helpful agent that tell funny jokes." }
    }

    @Bean
    open fun chatCompleterProvider() = ChatCompleterProvider {
        OpenAIRealtimeClient("wss://api.openai.com/v1/realtime?model=gpt-4o-realtime-preview-2024-10-01", key)
    }
}

val lastBinaryData = AtomicReference<String>()
