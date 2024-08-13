package io.github.lmos.arc.graphql.inbound


import io.github.lmos.arc.agents.conversation.AssistantMessage
import io.github.lmos.arc.agents.conversation.UserMessage
import io.github.lmos.arc.api.Message

fun List<Message>.convert() = map {
    when (it.role) {
        "user" -> UserMessage(it.content)
        "assistant" -> AssistantMessage(it.content)
        else -> throw IllegalArgumentException("Unknown role: ${it.role}")
    }
}

fun AssistantMessage?.toMessage() = Message("assistant", this?.content ?: "", turnId = this?.turnId)
