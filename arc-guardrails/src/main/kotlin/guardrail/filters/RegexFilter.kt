package ai.ancf.lmos.arc.guardrail.filters

import ai.ancf.lmos.arc.agents.conversation.ConversationMessage
import ai.ancf.lmos.arc.agents.dsl.AgentFilter

class RegexFilter(pattern: String, private val replacement: String) : AgentFilter {
    private val regex = Regex(pattern)
    override suspend fun filter(message: ConversationMessage): ConversationMessage {
        val content = message.content.replace(regex, replacement)
        return message.update(content)
    }
}
