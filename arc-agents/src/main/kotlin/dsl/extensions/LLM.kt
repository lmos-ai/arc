// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.agents.dsl.extensions

import io.github.lmos.arc.agents.AIException
import io.github.lmos.arc.agents.conversation.AssistantMessage
import io.github.lmos.arc.agents.conversation.SystemMessage
import io.github.lmos.arc.agents.conversation.UserMessage
import io.github.lmos.arc.agents.dsl.DSLContext
import io.github.lmos.arc.agents.dsl.get
import io.github.lmos.arc.agents.llm.ChatCompleterProvider
import io.github.lmos.arc.agents.llm.ChatCompletionSettings
import io.github.lmos.arc.core.result

/**
 * Extensions enabling accessing LLMs in the DSLContext.
 */
suspend fun DSLContext.llm(
    userMessage: String? = null,
    systemMessage: String? = null,
    model: String? = null,
    settings: ChatCompletionSettings? = null,
) = result<AssistantMessage, AIException> {
    val chatCompleterProvider = get<ChatCompleterProvider>()
    val chatCompleter = chatCompleterProvider.provideByModel(model = model)
    val messages = buildList {
        if (systemMessage != null) add(SystemMessage(systemMessage))
        if (userMessage != null) add(UserMessage(userMessage))
    }
    return chatCompleter.complete(messages, null, settings = settings)
}
