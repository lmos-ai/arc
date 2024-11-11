// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.agents.events

import ai.ancf.lmos.arc.agents.conversation.AssistantMessage
import kotlinx.coroutines.channels.Channel

/**
 * Publishes a message that is returned to the client.
 */
fun interface MessagePublisher {
    suspend fun publish(message: AssistantMessage)
}

fun interface MessageChannel {
    fun publish(message: AssistantMessage)
}

class MessagePublisherChannel(val channel: Channel<AssistantMessage>) : MessagePublisher {

    override suspend fun publish(message: AssistantMessage) {
        channel.trySend(message)
    }
}
