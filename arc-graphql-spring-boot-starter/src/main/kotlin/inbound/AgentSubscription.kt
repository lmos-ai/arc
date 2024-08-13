package io.github.lmos.arc.graphql.inbound

import com.expediagroup.graphql.server.operations.Subscription
import io.github.lmos.arc.agents.AgentProvider
import io.github.lmos.arc.agents.ChatAgent
import io.github.lmos.arc.agents.User
import io.github.lmos.arc.agents.conversation.AssistantMessage
import io.github.lmos.arc.agents.conversation.Conversation
import io.github.lmos.arc.agents.conversation.latest
import io.github.lmos.arc.api.AgentRequest
import io.github.lmos.arc.api.AgentResult
import io.github.lmos.arc.core.getOrThrow
import io.github.lmos.arc.core.onFailure
import io.github.lmos.arc.graphql.context.AnonymizationEntities
import io.github.lmos.arc.graphql.withLogContext
import kotlinx.coroutines.flow.flow

class AgentSubscription(
    private val agentProvider: AgentProvider,
) : Subscription {

    fun agent(request: AgentRequest) = flow {
        val agent = agentProvider.getAgents().firstOrNull() as ChatAgent? ?: error("No Agent defined!")
        val anonymizationEntities =
            AnonymizationEntities(request.conversationContext.anonymizationEntities ?: emptyList())

        val result = withLogContext(request) {
            agent.execute(
                Conversation(
                    user = User(request.userContext.userId),
                    transcript = request.messages.convert(),
                ),
                setOf(
                    request,
                    anonymizationEntities,
                    request.userContext,
                    request.systemContext,
                ),
            ).onFailure { ex -> ex.cause?.let { throw it } }.getOrThrow()
        }
        emit(
            AgentResult(
                messages = listOf(result.latest<AssistantMessage>().toMessage()),
                anonymizationEntities = anonymizationEntities.entities,
            ),
        )
    }
}
