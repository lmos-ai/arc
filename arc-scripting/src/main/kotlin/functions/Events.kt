// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.scripting.functions

import ai.ancf.lmos.arc.agents.events.BaseEvent
import ai.ancf.lmos.arc.agents.events.Event

/**
 * Events published when a function is loaded.
 */
data class FunctionLoadedEvent(
    val name: String,
    val errorMessage: String? = null,
) : Event by BaseEvent()
