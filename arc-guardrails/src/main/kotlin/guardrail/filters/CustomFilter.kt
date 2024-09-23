package ai.ancf.lmos.arc.guardrail.filters

import ai.ancf.lmos.arc.agents.conversation.ConversationMessage
import ai.ancf.lmos.arc.agents.dsl.AgentFilter

class CustomFilter(private val replacements: Map<String, String>) : AgentFilter {
    override suspend fun filter(message: ConversationMessage): ConversationMessage {
        var content = message.content
        for ((pattern, replacement) in replacements) {
            content = content.replace(Regex(pattern), replacement)
        }
        return message.update(content)
    }
}
