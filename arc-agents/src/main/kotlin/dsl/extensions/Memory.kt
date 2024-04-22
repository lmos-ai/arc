// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.agents.dsl.extensions

import io.github.lmos.arc.agents.conversation.Conversation
import io.github.lmos.arc.agents.dsl.DSLContext
import io.github.lmos.arc.agents.dsl.extensions.MemoryScope.LONG_TERM
import io.github.lmos.arc.agents.dsl.extensions.MemoryScope.SHORT_TERM
import io.github.lmos.arc.agents.dsl.get
import io.github.lmos.arc.agents.memory.Memory

enum class MemoryScope {
    SHORT_TERM,
    LONG_TERM,
}

/**
 * Extensions that add memory functions to the DSL context.
 */
suspend fun DSLContext.memory(key: String, value: Any?, scope: MemoryScope = SHORT_TERM) {
    val conversation = get<Conversation>()
    val memory = get<Memory>()
    when (scope) {
        SHORT_TERM -> memory.storeShortTerm(conversation.user.id, key, value, conversation.conversationId)
        LONG_TERM -> memory.storeLongTerm(conversation.user.id, key, value)
    }
}

suspend fun DSLContext.memory(key: String): Any? {
    val conversation = get<Conversation>()
    return get<Memory>().fetch(conversation.user.id, key, conversation.conversationId)
}
