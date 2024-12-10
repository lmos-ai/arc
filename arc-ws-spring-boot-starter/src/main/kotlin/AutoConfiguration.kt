// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.ws

import ai.ancf.lmos.arc.agents.AgentProvider
import ai.ancf.lmos.arc.ws.inbound.StreamingEndpoint
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.web.reactive.HandlerMapping
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping
import org.springframework.web.reactive.socket.server.WebSocketService
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter

@AutoConfiguration
open class AgentWSAutoConfiguration {

    @Bean
    fun handlerAdapter() =
        WebSocketHandlerAdapter(webSocketService())

    @Bean
    fun webSocketService(): WebSocketService {
        val strategy = org.springframework.web.reactive.socket.server.upgrade.TomcatRequestUpgradeStrategy().apply {
            setMaxBinaryMessageBufferSize(1024 * 1024 * 35) // 15MB
        }
        return HandshakeWebSocketService(strategy)
    }

    @Bean
    fun handlerMapping(streamingEndpoint: StreamingEndpoint): HandlerMapping {
        val map = mapOf("/ws/agent" to streamingEndpoint)
        val order = -1
        return SimpleUrlHandlerMapping(map, order)
    }

    @Bean
    fun streamingEndpoint(
        agentProvider: AgentProvider,
        errorHandler: ErrorHandler? = null,
        contextHandler: ContextHandler? = null,
        agentResolver: AgentResolver? = null,
    ) = StreamingEndpoint(agentProvider, errorHandler, contextHandler ?: EmptyContextHandler(), agentResolver)
}
