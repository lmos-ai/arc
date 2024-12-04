// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.agents

import ai.ancf.lmos.arc.agents.conversation.Conversation
import ai.ancf.lmos.arc.agents.events.BaseEvent
import ai.ancf.lmos.arc.agents.events.Event
import ai.ancf.lmos.arc.core.Result
import kotlin.time.Duration

/**
 * Collection of events that can be published by Agents.
 */
sealed class AgentEvent : Event by BaseEvent() {
    abstract val agent: Agent<*, *>
}

data class AgentFinishedEvent(
    override val agent: Agent<*, *>,
    val model: String?,
    val input: Conversation,
    val output: Result<Conversation, AgentFailedException>,
    val duration: Duration,
    val flowBreak: Boolean = false,
    val tools: Set<String> = emptySet(),
) : AgentEvent()

data class AgentStartedEvent(
    override val agent: Agent<*, *>,
) : AgentEvent()
