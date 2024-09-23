package ai.ancf.lmos.arc.guardrail.filters

import ai.ancf.lmos.arc.agents.conversation.ConversationMessage
import ai.ancf.lmos.arc.agents.dsl.AgentFilter

class ProfanityFilter(private val replacements: Map<String, String>) : AgentFilter {
    override suspend fun filter(message: ConversationMessage): ConversationMessage {
        var content = message.content
        for ((badWord, replacement) in replacements) {
            content = content.replace(badWord, replacement, ignoreCase = true)
        }
        return message.update(content)
    }
}
