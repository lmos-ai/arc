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
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.Timer
import java.math.RoundingMode.DOWN
import kotlin.time.Duration
import kotlin.time.toJavaDuration

/**
 * Converts events from the Arc Framework into performance metrics.
 */
class MetricsHandler(private val metrics: MeterRegistry) : EventHandler<Event> {

    override fun onEvent(event: Event) {
        when (event) {
            is AgentFinishedEvent -> with(event) {
                if (event.output is Success) {
                    timer(
                        "arc.agent.finished",
                        duration,
                        tags = mapOf("agent" to agent.name, "model" to (model ?: "default"))
                    )
                } else {
                    timer(
                        "arc.agent.failed",
                        duration,
                        tags = mapOf("agent" to agent.name)
                    )
                }
            }

            is LLMFinishedEvent -> with(event) {
                timer(
                    "arc.llm.finished",
                    duration,
                    tags = mapOf("model" to model)
                )
            }

            is RouterRoutedEvent -> with(event) {
                val accuracy = destination?.accuracy?.toBigDecimal()?.setScale(1, DOWN)?.toString() ?: "-1"
                val destination = destination?.destination ?: "null"
                timer(
                    "arc.router.routed",
                    duration,
                    tags = mapOf("accuracy" to accuracy, "destination" to destination)
                )
            }
        }
    }

    private fun timer(name: String, duration: Duration, tags: Map<String, String>) = Timer.builder(name)
        .tags(tags.map { (k, v) -> Tag.of(k, v) })
        .distributionStatisticExpiry(java.time.Duration.ofMinutes(5))
        .distributionStatisticBufferLength(50)  //limit memory usage
        .publishPercentiles(0.5, 0.95)
        .percentilePrecision(2)
        .register(metrics)
        .record(duration.toJavaDuration())
}
