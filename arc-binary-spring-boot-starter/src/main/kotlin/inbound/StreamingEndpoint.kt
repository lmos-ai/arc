// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.ws.inbound

import ai.ancf.lmos.arc.agent.client.ws.RequestEnvelope
import ai.ancf.lmos.arc.agents.AgentProvider
import ai.ancf.lmos.arc.agents.ChatAgent
import ai.ancf.lmos.arc.agents.User
import ai.ancf.lmos.arc.agents.conversation.AssistantMessage
import ai.ancf.lmos.arc.agents.conversation.Conversation
import ai.ancf.lmos.arc.agents.conversation.latest
import ai.ancf.lmos.arc.agents.events.MessagePublisherChannel
import ai.ancf.lmos.arc.agents.getAgentByName
import ai.ancf.lmos.arc.api.AgentRequest
import ai.ancf.lmos.arc.api.AgentResult
import ai.ancf.lmos.arc.core.Failure
import ai.ancf.lmos.arc.core.Success
import ai.ancf.lmos.arc.core.getOrThrow
import ai.ancf.lmos.arc.ws.AgentResolver
import ai.ancf.lmos.arc.ws.ContextHandler
import ai.ancf.lmos.arc.ws.EmptyContextHandler
import ai.ancf.lmos.arc.ws.ErrorHandler
import ai.ancf.lmos.arc.ws.context.AnonymizationEntities
import ai.ancf.lmos.arc.ws.context.ContextProvider
import ai.ancf.lmos.arc.ws.withLogContext
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.channelFlow
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.springframework.web.socket.BinaryMessage
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.AbstractWebSocketHandler
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap


class StreamingEndpoint(
    private val agentProvider: AgentProvider,
    private val errorHandler: ErrorHandler? = null,
    private val contextHandler: ContextHandler = EmptyContextHandler(),
    private val agentResolver: AgentResolver? = null,
) : AbstractWebSocketHandler() {

    private data class Session(val id: String, val requestEnvelope: RequestEnvelope?, val data: List<ByteArray>)

    private val sessions = ConcurrentHashMap<String, Session>()
    private val json = Json { }
    private val log = LoggerFactory.getLogger(javaClass)

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        println(message.payload)
        //val requestEnvelope = json.decodeFromString(RequestEnvelope.serializer(), message.payload)
        sessions[session.id] = Session(session.id, null, emptyList())
    }

    override fun handleBinaryMessage(session: WebSocketSession, message: BinaryMessage) {
        println(message.payload)
        sessions[session.id]?.let {
            sessions[session.id] = it.copy(data = it.data + message.payload.array())
            if (message.isLast) {
                //agent(it.requestEnvelope.agentName, it.requestEnvelope.agentRequest, DataProvider(it.data))
                log.info("Received last")
            }
        }
    }

    private fun agent(agentName: String? = null, request: AgentRequest, dataProvider: DataProvider) = channelFlow {
        coroutineScope {
            val agent = findAgent(agentName, request)
            val anonymizationEntities =
                AnonymizationEntities(request.conversationContext.anonymizationEntities.convertConversationEntities())
            val start = System.nanoTime()
            val messageChannel = Channel<AssistantMessage>()

            log.info("Received request: ${request.systemContext}")

            async {
                sendIntermediateMessage(messageChannel, start, anonymizationEntities)
            }

            val result = contextHandler.inject(request) {
                withLogContext(agent.name, request) {
                    agent.execute(
                        Conversation(
                            user = request.userContext?.userId?.let { User(it) },
                            conversationId = request.conversationContext.conversationId,
                            currentTurnId = request.conversationContext.turnId
                                ?: request.messages.lastOrNull()?.turnId
                                ?: request.messages.size.toString(),
                            transcript = request.messages.convert(),
                            anonymizationEntities = anonymizationEntities.entities,
                        ),
                        setOf(
                            request,
                            anonymizationEntities,
                            MessagePublisherChannel(messageChannel),
                            ContextProvider(request),
                            dataProvider,
                        ),
                    )
                }
            }

            val responseTime = Duration.ofNanos(System.nanoTime() - start).toMillis() / 1000.0
            when (result) {
                is Success -> send(
                    AgentResult(
                        status = result.value.classification.toString(),
                        responseTime = responseTime,
                        messages = listOf(result.value.latest<AssistantMessage>().toMessage()),
                        anonymizationEntities = anonymizationEntities.entities.convertAPIEntities(),
                    ),
                )

                is Failure -> {
                    val handledResult = (errorHandler?.handleError(result.reason) ?: result).getOrThrow()
                    send(
                        AgentResult(
                            responseTime = responseTime,
                            messages = listOf(handledResult.toMessage()),
                            anonymizationEntities = emptyList(),
                        ),
                    )
                }
            }
            messageChannel.close()
        }
    }

    private fun findAgent(agentName: String?, request: AgentRequest): ChatAgent =
        agentName?.let { agentProvider.getAgentByName(it) } as ChatAgent?
            ?: agentResolver?.resolveAgent(agentName, request) as ChatAgent?
            ?: agentProvider.getAgents().firstOrNull() as ChatAgent?
            ?: error("No Agent defined!")

    private suspend fun ProducerScope<AgentResult>.sendIntermediateMessage(
        messageChannel: Channel<AssistantMessage>,
        startTime: Long,
        anonymizationEntities: AnonymizationEntities,
    ) {
        for (message in messageChannel) {
            log.debug("Sending intermediate message: $message")
            val responseTime = Duration.ofNanos(System.nanoTime() - startTime).toMillis() / 1000.0
            trySend(
                AgentResult(
                    responseTime = responseTime,
                    messages = listOf(message.toMessage()),
                    anonymizationEntities = anonymizationEntities.entities.convertAPIEntities(),
                ),
            )
        }
    }
}

