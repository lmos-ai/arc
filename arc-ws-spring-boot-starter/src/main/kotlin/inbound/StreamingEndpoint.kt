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
import ai.ancf.lmos.arc.api.REQUEST_END
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.asFlux
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.reactive.socket.WebSocketHandler
import org.springframework.web.reactive.socket.WebSocketMessage
import org.springframework.web.reactive.socket.WebSocketMessage.Type.BINARY
import org.springframework.web.reactive.socket.WebSocketMessage.Type.TEXT
import org.springframework.web.reactive.socket.WebSocketSession
import org.springframework.web.server.ServerWebExchange
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

class StreamingEndpoint(
    private val agentProvider: AgentProvider,
    private val errorHandler: ErrorHandler? = null,
    private val contextHandler: ContextHandler = EmptyContextHandler(),
    private val agentResolver: AgentResolver? = null,
) : WebSocketHandler, CorsConfigurationSource {

    private data class Session(val id: String, val envelope: RequestEnvelope, val data: WritableDataStream)

    private val sessions = ConcurrentHashMap<String, Session>()
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    private val log = LoggerFactory.getLogger(javaClass)

    override fun handle(session: WebSocketSession) = mono<Void> {
        try {
            session.receive().asFlow().collect { message ->
                when (message.type) {
                    TEXT -> handleTextMessage(session, message)
                    BINARY -> handleBinaryMessage(session, message)
                    else -> {}
                }
            }
        } catch (e: Exception) {
            log.error("Error handling session: ${session.id}", e)
        }
        null
    }

    private suspend fun handleTextMessage(session: WebSocketSession, message: WebSocketMessage) {
        val msg = message.payloadAsText
        log.info("Received message: $msg")

        if (REQUEST_END == msg) {
            log.info("Sending session: ${session.id}")
            val requestSession = sessions[session.id] ?: run {
                log.error("No session found for: ${session.id}")
                return
            }
            val result = callAgent(
                requestSession.envelope.agentName,
                requestSession.envelope.payload,
                requestSession.data,
            ).map {
                val jsonResult = json.encodeToString(AgentResult.serializer(), it)
                session.textMessage(jsonResult)
            }
            session.send(result.asFlux()).awaitSingleOrNull()
            sessions[session.id]?.data?.close()
            sessions.remove(session.id)
        } else {
            val envelope = json.decodeFromString(RequestEnvelope.serializer(), msg)
            log.info("Stored session: ${session.id}")
            sessions[session.id] = Session(session.id, envelope, WritableDataStream())
        }
    }

    private suspend fun handleBinaryMessage(session: WebSocketSession, message: WebSocketMessage) {
        log.info("Received binary message for: ${session.id}")
        sessions[session.id]?.let {
            val bytes = message.payload.asInputStream().use { b -> b.readAllBytes() }
            sessions[session.id]?.data?.send(bytes)
        }
    }

    private fun callAgent(agentName: String? = null, request: AgentRequest, dataProvider: WritableDataStream) =
        channelFlow {
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
                                transcript = request.messages.convert(dataProvider),
                                anonymizationEntities = anonymizationEntities.entities,
                            ),
                            setOf(
                                request,
                                anonymizationEntities,
                                MessagePublisherChannel(messageChannel),
                                ContextProvider(request),
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

    override fun getCorsConfiguration(exchange: ServerWebExchange): CorsConfiguration? {
        return CorsConfiguration().apply {
            allowCredentials = false
            allowedOrigins = listOf("*")
            allowedMethods = listOf("*")
            allowedHeaders = listOf("*")
            maxAge = 3600
        }
    }
}
