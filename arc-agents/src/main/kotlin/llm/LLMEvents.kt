// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.agents.llm

import io.github.lmos.arc.agents.events.Event
import java.time.Instant
import kotlin.time.Duration

/**
 * Events published by the LLM clients.
 */

data class LLMStartedEvent(
    val model: String,
    override val timestamp: Instant = Instant.now(),
) : Event

data class LLMFinishedEvent(
    val model: String,
    val totalTokens: Int,
    val promptTokens: Int,
    val completionTokens: Int,
    val functionCallCount: Int,
    val duration: Duration,
    override val timestamp: Instant = Instant.now(),
) : Event
