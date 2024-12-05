// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.ws

import ai.ancf.lmos.arc.agents.AgentProvider
import ai.ancf.lmos.arc.ws.inbound.StreamingEndpoint
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.PropertySource
import org.springframework.core.io.ClassPathResource
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.socket.config.annotation.EnableWebSocket
import org.springframework.web.socket.config.annotation.WebSocketConfigurer
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry


@AutoConfiguration
@PropertySource("classpath:arc.properties")
@EnableWebSocket
@Import(CC::class)
open class AgentGraphQLAutoConfiguration {

    @Bean
    fun corsWebFilter(): CorsWebFilter {
        val corsConfig = CorsConfiguration()
        corsConfig.allowedOrigins = mutableListOf("*")
        corsConfig.maxAge = 8000L

        val source = org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfig)
        return CorsWebFilter(source)
    }

    @Bean
    fun streamingEndpoint(
        agentProvider: AgentProvider,
        errorHandler: ErrorHandler? = null,
        contextHandler: ContextHandler? = null,
        agentResolver: AgentResolver? = null,
    ) = StreamingEndpoint(agentProvider, errorHandler, contextHandler ?: EmptyContextHandler(), agentResolver)


    @Bean
    @ConditionalOnProperty("arc.chat.ui.enabled", havingValue = "true")
    fun chatResourceRouter() = RouterFunctions.resources("/chat/**", ClassPathResource("chat/"))
}


@Configuration
open class CC : WebSocketConfigurer {

    @Autowired
    lateinit var streamingEndpoint: StreamingEndpoint

    override fun registerWebSocketHandlers(registry: WebSocketHandlerRegistry) {
        registry.addHandler(streamingEndpoint, "/ws/agent")
    }
}


