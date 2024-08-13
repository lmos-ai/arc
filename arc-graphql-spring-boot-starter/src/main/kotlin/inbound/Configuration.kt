package io.github.lmos.arc.graphql.inbound

import io.github.lmos.arc.agents.AgentProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class InboundConfiguration {

    @Bean
    fun agentQuery(agentProvider: AgentProvider) = AgentQuery(agentProvider)

    @Bean
    fun agentSubscription(agentProvider: AgentProvider) = AgentSubscription(agentProvider)
}