// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.graphql.inbound

import ai.ancf.lmos.arc.agents.AgentProvider
import ai.ancf.lmos.arc.agents.ChatAgent
import ai.ancf.lmos.arc.agents.User
import ai.ancf.lmos.arc.agents.conversation.AssistantMessage
import ai.ancf.lmos.arc.agents.conversation.Conversation
import ai.ancf.lmos.arc.agents.conversation.latest
import ai.ancf.lmos.arc.agents.getAgentByName
import ai.ancf.lmos.arc.api.AgentRequest
import ai.ancf.lmos.arc.api.AgentResult
import ai.ancf.lmos.arc.core.Failure
import ai.ancf.lmos.arc.core.Success
import ai.ancf.lmos.arc.core.getOrThrow
import ai.ancf.lmos.arc.graphql.*
import ai.ancf.lmos.arc.graphql.context.AnonymizationEntities
import com.expediagroup.graphql.server.operations.Subscription
import kotlinx.coroutines.flow.flow
import org.slf4j.LoggerFactory
import java.time.Duration

class AgentSubscription(
    private val agentProvider: AgentProvider,
    private val errorHandler: ErrorHandler? = null,
    private val contextHandler: ContextHandler = EmptyContextHandler(),
    private val agentResolver: AgentResolver? = null,
) : Subscription {

    private val log = LoggerFactory.getLogger(javaClass)

    fun agent(agentName: String? = null, request: AgentRequest) = flow {
        val agent = findAgent(agentName, request)
        val anonymizationEntities =
            AnonymizationEntities(request.conversationContext.anonymizationEntities.convertConversationEntities())
        val start = System.nanoTime()

        log.info("Received request: ${request.systemContext}")

        val result = contextHandler.inject(request) {
            withLogContext(agent.name, request) {
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

        val responseTime = Duration.ofNanos(System.nanoTime() - start).toMillis() / 1000.0
        when (result) {
            is Success -> emit(
                AgentResult(
                    responseTime = responseTime,
                    messages = listOf(result.value.latest<AssistantMessage>().toMessage()),
                    anonymizationEntities = anonymizationEntities.entities.convertAPIEntities(),
                ),
            )

            is Failure -> {
                val handledResult = (errorHandler?.handleError(result.reason) ?: result).getOrThrow()
                emit(
                    AgentResult(
                        responseTime = responseTime,
                        messages = listOf(handledResult.toMessage()),
                        anonymizationEntities = emptyList(),
                    ),
                )
            }
        }
    }

    private fun findAgent(agentName: String?, request: AgentRequest): ChatAgent =
        agentName?.let { agentProvider.getAgentByName(it) } as ChatAgent?
            ?: agentResolver?.resolveAgent(request) as ChatAgent?
            ?: agentProvider.getAgents().firstOrNull() as ChatAgent?
            ?: error("No Agent defined!")
}
