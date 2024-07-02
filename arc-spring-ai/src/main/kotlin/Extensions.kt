package io.github.lmos.arc.spring.ai

import io.github.lmos.arc.agents.conversation.AssistantMessage
import io.github.lmos.arc.agents.conversation.ConversationMessage
import io.github.lmos.arc.agents.conversation.SystemMessage
import io.github.lmos.arc.agents.conversation.UserMessage

/**
 * Converts a list of ConversationMessages to a list of Spring AI messages.
 */
fun List<ConversationMessage>.toSpringAI() = map { msg ->
    when (msg) {
        is UserMessage -> org.springframework.ai.chat.messages.UserMessage(msg.content)
        is AssistantMessage -> org.springframework.ai.chat.messages.AssistantMessage(msg.content)
        is SystemMessage -> org.springframework.ai.chat.messages.SystemMessage(msg.content)
    }
}
