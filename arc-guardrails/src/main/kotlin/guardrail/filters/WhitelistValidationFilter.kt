package ai.ancf.lmos.arc.guardrail.filters

import ai.ancf.lmos.arc.agents.conversation.ConversationMessage
import ai.ancf.lmos.arc.agents.dsl.AgentFilter
import ai.ancf.lmos.arc.agents.dsl.DSLContext
import ai.ancf.lmos.arc.agents.dsl.extensions.breakWith

// Whitelist Validation Filter
class WhitelistValidationFilter(
    private val context: DSLContext,
    private val disallowedPattern: String?,
    private val whitelist: List<String>,
    private val errorMessage: String,
) : AgentFilter {

    override suspend fun filter(message: ConversationMessage): ConversationMessage {
        // check if all matching patterns are in the whitelist
        if (disallowedPattern != null) {
            val regex = Regex(disallowedPattern)
            val matches = regex.findAll(message.content)
            for (match in matches) {
                if (!whitelist.contains(match.value)) {
                    context.breakWith(
                        message = "Sorry, I am currently unable to process your request.",
                        reason = errorMessage,
                    )
                }
            }
        }
        return message
    }
}
