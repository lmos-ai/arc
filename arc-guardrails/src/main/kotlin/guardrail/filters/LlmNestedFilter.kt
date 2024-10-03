package ai.ancf.lmos.arc.guardrail.filters

import ai.ancf.lmos.arc.agents.ArcException
import ai.ancf.lmos.arc.agents.conversation.AssistantMessage
import ai.ancf.lmos.arc.agents.conversation.ConversationMessage
import ai.ancf.lmos.arc.agents.dsl.AgentFilter
import ai.ancf.lmos.arc.agents.dsl.DSLContext
import ai.ancf.lmos.arc.agents.dsl.extensions.llm
import ai.ancf.lmos.arc.agents.dsl.extensions.memory
import ai.ancf.lmos.arc.core.Result
import ai.ancf.lmos.arc.core.getOrThrow
import ai.ancf.lmos.arc.guardrail.dsl.LlmBodyBuilder

class LlmNestedFilter(
    private val body: LlmBodyBuilder,
    private val nestedFilters: List<AgentFilter>,
    private val context: DSLContext,
) : AgentFilter {
    override suspend fun filter(message: ConversationMessage): ConversationMessage? {
        val llmResponse: Result<AssistantMessage, ArcException> = context.llm(
            userMessage = body.userMessage,
            systemMessage = body.systemMessage,
            settings = body.settings,
            model = body.model,
        )
        // Store LLM response in context
        val llmKey = "llm_response_${message.turnId}"
        context.memory(llmKey, llmResponse.getOrThrow())

        // Apply nested guardrails
        var currentMessage: ConversationMessage? = message
        for (filter in nestedFilters) {
            currentMessage = currentMessage?.let { filter.filter(it) }
            if (currentMessage == null) break
        }

        return currentMessage
    }
}