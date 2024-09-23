package ai.ancf.lmos.arc.guardrail.filters

import ai.ancf.lmos.arc.agents.conversation.ConversationMessage
import ai.ancf.lmos.arc.agents.dsl.AgentFilter

// Whitelist Validation Filter
class WhitelistValidationFilter(
    private val whitelist: List<String>,
    private val errorMessage: String,
) : AgentFilter {
    override suspend fun filter(message: ConversationMessage): ConversationMessage {
        val content = message.content
        // Check if content contains any value not in the whitelist
        val words = content.split("\\s+".toRegex())
        for (word in words) {
            if (word !in whitelist) {
                // Raise an error or handle it accordingly
                throw IllegalArgumentException("$errorMessage Found: $word")
            }
        }
        return message
    }
}
