// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.scripting.agents

import ai.ancf.lmos.arc.agents.events.BaseEvent
import ai.ancf.lmos.arc.agents.events.Event

/**
 * Events published when an agent is loaded.
 */
data class AgentLoadedEvent(
    val name: String,
    val errorMessage: String? = null,
) : Event by BaseEvent()
