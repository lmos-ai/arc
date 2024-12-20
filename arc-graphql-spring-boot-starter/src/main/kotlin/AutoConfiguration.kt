// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.graphql

import ai.ancf.lmos.arc.agents.AgentProvider
import ai.ancf.lmos.arc.graphql.inbound.AgentQuery
import ai.ancf.lmos.arc.graphql.inbound.AgentSubscription
import com.expediagroup.graphql.server.spring.GraphQLAutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.PropertySource
import org.springframework.core.io.ClassPathResource
import org.springframework.web.reactive.function.server.RouterFunctions

@AutoConfiguration
@AutoConfigureBefore(GraphQLAutoConfiguration::class)
@Import(EventsConfiguration::class)
@PropertySource("classpath:arc.properties")
open class AgentGraphQLAutoConfiguration {

    @Bean
    fun agentQuery(agentProvider: AgentProvider) = AgentQuery(agentProvider)

    @Bean
    fun agentSubscription(
        agentProvider: AgentProvider,
        errorHandler: ErrorHandler? = null,
        contextHandler: ContextHandler? = null,
        agentResolver: AgentResolver? = null,
    ) = AgentSubscription(agentProvider, errorHandler, contextHandler ?: EmptyContextHandler(), agentResolver)

    @Bean
    @ConditionalOnProperty("arc.chat.ui.enabled", havingValue = "true")
    fun chatResourceRouter() = RouterFunctions.resources("/chat/**", ClassPathResource("chat/"))

    @Bean
    @ConditionalOnProperty("arc.cors.enabled", havingValue = "true")
    fun corsFilter() = CorsFilter()
}
