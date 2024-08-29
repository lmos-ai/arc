// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.agents.dsl.extensions

import ai.ancf.lmos.arc.agents.ArcException
import ai.ancf.lmos.arc.agents.conversation.AssistantMessage
import ai.ancf.lmos.arc.agents.conversation.SystemMessage
import ai.ancf.lmos.arc.agents.conversation.UserMessage
import ai.ancf.lmos.arc.agents.dsl.DSLContext
import ai.ancf.lmos.arc.agents.dsl.get
import ai.ancf.lmos.arc.agents.llm.ChatCompleterProvider
import ai.ancf.lmos.arc.agents.llm.ChatCompletionSettings
import ai.ancf.lmos.arc.core.result

/**
 * Extensions enabling accessing LLMs in the DSLContext.
 */
suspend fun DSLContext.llm(
    userMessage: String? = null,
    systemMessage: String? = null,
    model: String? = null,
    settings: ChatCompletionSettings? = null,
) = result<AssistantMessage, ArcException> {
    val chatCompleterProvider = get<ChatCompleterProvider>()
    val chatCompleter = chatCompleterProvider.provideByModel(model = model)
    val messages = buildList {
        if (systemMessage != null) add(SystemMessage(systemMessage))
        if (userMessage != null) add(UserMessage(userMessage))
    }
    return chatCompleter.complete(messages, null, settings = settings)
}
