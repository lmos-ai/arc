// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.agents.functions

import ai.ancf.lmos.arc.agents.ArcException
import ai.ancf.lmos.arc.agents.events.Event
import ai.ancf.lmos.arc.core.Result
import java.time.Instant
import kotlin.time.Duration

/**
 * Events published by the LLM clients when calling functions.
 */

data class LLMFunctionStartedEvent(
    val name: String,
    val param: Map<String, Any?>,
    override val timestamp: Instant = Instant.now(),
) : Event

data class LLMFunctionCalledEvent(
    val name: String,
    val param: Map<String, Any?>,
    val result: Result<String, ArcException>,
    val duration: Duration,
    override val timestamp: Instant = Instant.now(),
) : Event
