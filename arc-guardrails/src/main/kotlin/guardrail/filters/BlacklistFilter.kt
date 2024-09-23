package ai.ancf.lmos.arc.guardrail.filters

import ai.ancf.lmos.arc.agents.AgentFailedException
import ai.ancf.lmos.arc.agents.conversation.ConversationMessage
import ai.ancf.lmos.arc.agents.dsl.AgentFilter

class BlacklistFilter(private val blacklist: List<String>) : AgentFilter {
    override suspend fun filter(message: ConversationMessage): ConversationMessage {
        for (term in blacklist) {
            if (message.content.contains(term, ignoreCase = true)) {
                throw AgentFailedException("Message contains blacklisted term: $term")
            }
        }
        return message
    }
}
