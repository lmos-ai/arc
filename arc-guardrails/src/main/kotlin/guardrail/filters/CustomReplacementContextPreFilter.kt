// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.guardrail.filters

import ai.ancf.lmos.arc.agents.conversation.ConversationMessage
import ai.ancf.lmos.arc.agents.dsl.AgentFilter
import ai.ancf.lmos.arc.agents.dsl.DSLContext
import ai.ancf.lmos.arc.agents.dsl.extensions.memory

/**
 * A filter that replaces text in a message based on a map of replacements.
 * This filter supports incremental replacements based on a list of patterns.
 * The patterns are used to identify the text to be replaced, and the type is used to generate a unique placeholder.
 */
class CustomReplacementContextPreFilter(private val contextVariableName: String, private val context: DSLContext, private val patterns: List<Pair<String, String>>) : AgentFilter {

    override suspend fun filter(message: ConversationMessage): ConversationMessage {
        var content = context.memory(contextVariableName) as String
        val placeholders = mutableMapOf<String, String>() // placeholder -> original value
        val counts = mutableMapOf<String, Int>() // type -> count

        for ((pattern, type) in patterns) {
            val regex = Regex(pattern)
            content = regex.replace(content) { matchResult ->
                val count = counts.getOrDefault(type, 0) + 1
                counts[type] = count
                val placeholder = if (type.contains("{count}")) type.replace("{count}", count.toString()) else "$type-$count"
                placeholders[placeholder] = matchResult.value
                placeholder
            }
        }

        // Store placeholders in DSLContext using a unique key
        val mappingKey = getMappingKey(message)
        context.memory(mappingKey, placeholders)
        context.memory(contextVariableName, content)

        return message
    }

    private fun getMappingKey(message: ConversationMessage): String {
        return "placeholders_${message.turnId}"
    }
}
