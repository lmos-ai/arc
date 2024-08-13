package io.github.lmos.arc.graphql

import io.github.lmos.arc.graphql.inbound.InboundConfiguration
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@Import(
    InboundConfiguration::class
)
open class AgentConfiguration
