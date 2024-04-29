// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.agents.dsl.extensions

import io.github.lmos.arc.agents.conversation.ConversationMessage
import io.github.lmos.arc.agents.dsl.DSLContext

/**
 * Extensions for extracting data from messages.
 */

/**
 * Extracts email addresses from a message.
 */
val EMAIL_PATTERN =
    Regex("""([a-zA-Z0-9.!#${'$'}%&'*+/=?^_`{|}~-]+)@([a-zA-Z0-9](?:[a-zA-Z0-9-]{0,62}[a-zA-Z0-9])?\.)+[a-zA-Z]{2,}""")

fun DSLContext.extractEmail(message: ConversationMessage): List<String> {
    return EMAIL_PATTERN.findAll(message.content).map { it.value }.toList()
}

/**
 * Extracts urls from a message.
 */
val URL_PATTERN =
    Regex("""(https|http)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]""")

fun DSLContext.extractUrl(message: ConversationMessage): List<String> {
    return URL_PATTERN.findAll(message.content).map { it.value }.toList()
}
