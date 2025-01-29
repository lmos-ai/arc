// SPDX-FileCopyrightText: 2025 Deutsche Telekom AG and others
//
// SPDX-License-Identifier: Apache-2.0

package org.eclipse.lmos.arc.graphql

import com.expediagroup.graphql.server.spring.GraphQLAutoConfiguration
import org.eclipse.lmos.arc.agents.AgentProvider
import org.eclipse.lmos.arc.graphql.inbound.AccessControlHeaders
import org.eclipse.lmos.arc.graphql.inbound.AgentQuery
import org.eclipse.lmos.arc.graphql.inbound.AgentSubscription
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.context.annotation.PropertySource

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
    @ConditionalOnProperty("arc.cors.enabled", havingValue = "true")
    fun accessControlHeaders(
        @Value("\${arc.cors.allow-origin:*}") allowOrigin: String,
        @Value("\${arc.cors.allow-methods:POST}") allowMethods: String,
        @Value("\${arc.cors.allow-headers:Content-Type}") allowHeaders: String,
    ) = AccessControlHeaders(allowOrigin, allowMethods, allowHeaders)
}
