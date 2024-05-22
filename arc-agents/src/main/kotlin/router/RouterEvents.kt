// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.agents.router

import io.github.lmos.arc.agents.events.Event
import java.time.Instant
import kotlin.time.Duration

/**
 * Events published by the LLM clients.
 */

data class RouterReadyEvent(
    override val timestamp: Instant = Instant.now(),
) : Event

data class RouterRoutedEvent(
    val request: String,
    val destination: Destination?,
    val duration: Duration,
    override val timestamp: Instant = Instant.now(),
) : Event
