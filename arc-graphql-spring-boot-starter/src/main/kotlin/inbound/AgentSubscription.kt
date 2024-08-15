package io.github.lmos.arc.graphql.inbound

import com.expediagroup.graphql.server.operations.Subscription
import io.github.lmos.arc.agents.AgentProvider
import io.github.lmos.arc.agents.ChatAgent
import io.github.lmos.arc.agents.User
import io.github.lmos.arc.agents.conversation.AssistantMessage
import io.github.lmos.arc.agents.conversation.Conversation
import io.github.lmos.arc.agents.conversation.latest
import io.github.lmos.arc.agents.getAgentByName
import io.github.lmos.arc.api.AgentRequest
import io.github.lmos.arc.api.AgentResult
import io.github.lmos.arc.core.Failure
import io.github.lmos.arc.core.Success
import io.github.lmos.arc.core.getOrThrow
import io.github.lmos.arc.graphql.ContextHandler
import io.github.lmos.arc.graphql.EmptyContextHandler
import io.github.lmos.arc.graphql.ErrorHandler
import io.github.lmos.arc.graphql.context.AnonymizationEntities
import io.github.lmos.arc.graphql.withLogContext
import kotlinx.coroutines.flow.flow
import org.slf4j.LoggerFactory

class AgentSubscription(
    private val agentProvider: AgentProvider,
    private val errorHandler: ErrorHandler? = null,
    private val contextHandler: ContextHandler = EmptyContextHandler(),
) : Subscription {

    private val log = LoggerFactory.getLogger(javaClass)

    fun agent(agentName: String? = null, request: AgentRequest) = flow {
        val agent = findAgent(agentName)
        val anonymizationEntities =
            AnonymizationEntities(request.conversationContext.anonymizationEntities.convertConversationEntities())

        log.info("Received request: ${request.systemContext}")

        val result = contextHandler.inject(request) {
            withLogContext(request) {
                agent.execute(
                    Conversation(
                        user = User(request.userContext.userId),
                        transcript = request.messages.convert(),
                        anonymizationEntities = anonymizationEntities.entities,
                    ),
                    setOf(request, anonymizationEntities),
                )
            }
        }
        when (result) {
            is Success -> emit(
                AgentResult(
                    messages = listOf(result.value.latest<AssistantMessage>().toMessage()),
                    anonymizationEntities = anonymizationEntities.entities.convertAPIEntities(),
                ),
            )

            is Failure -> {
                val handledResult = (errorHandler?.handleError(result.reason) ?: result).getOrThrow()
                emit(
                    AgentResult(
                        messages = listOf(handledResult.toMessage()),
                        anonymizationEntities = emptyList(),
                    ),
                )
            }
        }
    }

    private fun findAgent(agentName: String?): ChatAgent =
        agentName?.let { agentProvider.getAgentByName(it) } as ChatAgent?
            ?: agentProvider.getAgents().firstOrNull() as ChatAgent?
            ?: error("No Agent defined!")
}
