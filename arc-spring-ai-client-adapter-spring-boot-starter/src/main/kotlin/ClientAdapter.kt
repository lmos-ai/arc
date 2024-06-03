// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

import io.github.lmos.arc.agents.ArcException
import io.github.lmos.arc.agents.conversation.AssistantMessage
import io.github.lmos.arc.agents.conversation.ConversationMessage
import io.github.lmos.arc.agents.conversation.SystemMessage
import io.github.lmos.arc.agents.conversation.UserMessage
import io.github.lmos.arc.agents.functions.LLMFunction
import io.github.lmos.arc.agents.llm.ChatCompleter
import io.github.lmos.arc.agents.llm.ChatCompletionSettings
import io.github.lmos.arc.core.failWith
import io.github.lmos.arc.core.result
import org.springframework.ai.chat.ChatClient
import org.springframework.ai.chat.messages.ChatMessage
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.MessageType
import org.springframework.ai.chat.prompt.ChatOptions
import org.springframework.ai.chat.prompt.Prompt

private fun messageConversion(msg: ConversationMessage): Message {
    val role = when (msg) {
        is UserMessage -> MessageType.USER
        is AssistantMessage -> MessageType.ASSISTANT
        is SystemMessage -> MessageType.SYSTEM
    }
    return ChatMessage(role, msg.content)
}

private class SpringAiChatOptionsAdapter(private val settings: ChatCompletionSettings) : ChatOptions {
    override fun getTemperature(): Float {
        return settings.temperature!!.toFloat()
    }

    override fun getTopP(): Float {
        return settings.topP!!.toFloat()
    }

    override fun getTopK(): Int {
        throw UnsupportedOperationException("getTopK Not supported yet.")
    }
}

class SpringAiClient(private val client: ChatClient) : ChatCompleter {
    override suspend fun complete(
        messages: List<ConversationMessage>,
        functions: List<LLMFunction>?,
        settings: ChatCompletionSettings?,
    ) = result<AssistantMessage, ArcException> {
        val conversationMessages = messages.map { messageConversion(it) }

        val response = client.call(
            Prompt(
                conversationMessages,
                settings?.let { SpringAiChatOptionsAdapter(settings) },
            ),
        )
        response?.result?.output?.content?.let {
            return@result AssistantMessage(it)
        } ?: failWith {
            ArcException("Something went wrong while calling the HuggingFaceChatClient")
        }
    }
}
