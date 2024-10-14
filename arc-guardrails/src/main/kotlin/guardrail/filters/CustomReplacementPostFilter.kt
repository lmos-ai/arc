package ai.ancf.lmos.arc.guardrail.filters

import ai.ancf.lmos.arc.agents.conversation.ConversationMessage
import ai.ancf.lmos.arc.agents.dsl.AgentFilter
import ai.ancf.lmos.arc.agents.dsl.DSLContext
import ai.ancf.lmos.arc.agents.dsl.extensions.memory

/**
 * A filter that replaces text in a message based on a map of replacements.
 * This filter is used to replace placeholders in a message with their original values.
 * The placeholders are read from the DSLContext memory using a unique key.
 */
class CustomReplacementPostFilter(private val context: DSLContext) : AgentFilter {
    override suspend fun filter(message: ConversationMessage): ConversationMessage {
        var content = message.content
        val mappingKey = getMappingKey(message)
        val placeholders = context.memory(mappingKey) as? Map<String, String> ?: emptyMap()

        for ((placeholder, originalValue) in placeholders) {
            content = content.replace(placeholder, originalValue)
        }
        return message.update(content)
    }

    private fun getMappingKey(message: ConversationMessage): String {
        return "placeholders_${message.turnId}"
    }
}
