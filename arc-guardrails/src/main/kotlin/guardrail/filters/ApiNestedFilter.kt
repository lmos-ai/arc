package ai.ancf.lmos.arc.guardrail.filters

import ai.ancf.lmos.arc.agents.conversation.ConversationMessage
import ai.ancf.lmos.arc.agents.dsl.AgentFilter
import ai.ancf.lmos.arc.agents.dsl.DSLContext
import ai.ancf.lmos.arc.agents.dsl.extensions.memory
import ai.ancf.lmos.arc.guardrail.dsl.ApiBodyBuilder

/**
 * A filter that makes an API request and applies a list of nested filters to the response.
 */
class ApiNestedFilter(
    private val url: String,
    private val bodyBuilder: ApiBodyBuilder,
    private val nestedFilters: List<AgentFilter>,
    private val context: DSLContext,
) : AgentFilter {
    override suspend fun filter(message: ConversationMessage): ConversationMessage? {
        val urlWithQueryParams = bodyBuilder.params.fold(url) { acc, param ->
            val (name, value) = param as ApiParam.Query
            "$acc&$name=$value"
        }

        val apiResponse = context.httpGet(urlWithQueryParams)

        // Store API response in context
        val apiKey = "api_response_${message.turnId}"
        context.memory(apiKey, apiResponse)

        // Apply nested guardrails
        var currentMessage: ConversationMessage? = message
        for (filter in nestedFilters) {
            currentMessage = currentMessage?.let { filter.filter(it) }
            if (currentMessage == null) break
        }

        return currentMessage
    }
}
