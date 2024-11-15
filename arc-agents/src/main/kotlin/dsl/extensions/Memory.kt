// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.agents.dsl.extensions

import ai.ancf.lmos.arc.agents.conversation.Conversation
import ai.ancf.lmos.arc.agents.dsl.DSLContext
import ai.ancf.lmos.arc.agents.dsl.extensions.MemoryScope.LONG_TERM
import ai.ancf.lmos.arc.agents.dsl.extensions.MemoryScope.SHORT_TERM
import ai.ancf.lmos.arc.agents.dsl.get
import ai.ancf.lmos.arc.agents.memory.Memory

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
    val userId = conversation.user?.id

    if (LONG_TERM == scope && userId == null) {
        kotlin.error("LONG_TERM is only available when a user id is present!")
    }

    when (scope) {
        SHORT_TERM -> memory.storeShortTerm(userId ?: "unknown", key, value, conversation.conversationId)
        LONG_TERM -> memory.storeLongTerm(userId!!, key, value)
    }
}

suspend fun DSLContext.memory(key: String): Any? {
    val conversation = get<Conversation>()
    val memory = get<Memory>()
    val userId = conversation.user?.id
    return memory.fetch(userId ?: "unknown", key, conversation.conversationId)
}
