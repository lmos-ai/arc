// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0
package io.github.lmos.arc.spring

import io.github.lmos.arc.agents.AgentFinishedEvent
import io.github.lmos.arc.agents.events.Event
import io.github.lmos.arc.agents.events.EventHandler
import io.github.lmos.arc.agents.llm.LLMFinishedEvent
import io.github.lmos.arc.agents.router.RouterRoutedEvent
import io.github.lmos.arc.core.Success
import io.micrometer.core.instrument.MeterRegistry
import kotlin.time.toJavaDuration

/**
 * Converts events from the Arc Framework into performance metrics.
 */
class MetricsHandler(private val metrics: MeterRegistry) : EventHandler<Event> {

    override fun onEvent(event: Event) {
        when (event) {
            is AgentFinishedEvent -> {
                if (event.output is Success) {
                    metrics.timer(
                        "arc.agent.finished",
                        "agent",
                        event.agent.name,
                        "model",
                        event.model ?: "default",
                    ).record(event.duration.toJavaDuration())
                } else {
                    metrics.timer(
                        "arc.agent.failed",
                        "agent",
                        event.agent.name,
                    ).record(event.duration.toJavaDuration())
                }
            }

            is LLMFinishedEvent -> {
                metrics.timer(
                    "arc.llm.finished",
                    "model",
                    event.model,
                ).record(event.duration.toJavaDuration())
            }

            is RouterRoutedEvent -> {
                metrics.timer(
                    "arc.router.routed",
                    "accuracy",
                    event.destination?.accuracy?.toString() ?: "-1",
                    "destination",
                    event.destination?.destination ?: "null",
                ).record(event.duration.toJavaDuration())
            }
        }
    }
}
