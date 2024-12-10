// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.ws.inbound

import ai.ancf.lmos.arc.agents.conversation.WritableDataStream
import ai.ancf.lmos.arc.agents.conversation.readAllBytes
import ai.ancf.lmos.arc.api.AgentResult
import ai.ancf.lmos.arc.api.REQUEST_END
import ai.ancf.lmos.arc.api.RequestEnvelope
import ai.ancf.lmos.arc.ws.AgentCaller
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactor.asFlux
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
import java.util.concurrent.ConcurrentHashMap

/**
 * A WebSocket handler that streams audio data to an agent and returns the result.
 */
class StreamingEndpoint(private val agentCaller: AgentCaller) : WebSocketHandler, CorsConfigurationSource {

    private data class Session(val id: String, val envelope: RequestEnvelope, val data: WritableDataStream)

    private val sessions = ConcurrentHashMap<String, Session>()
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    private val log = LoggerFactory.getLogger(javaClass)

    override fun handle(session: WebSocketSession) = mono<Void> {
        val outChannel = Channel<WebSocketMessage>(capacity = Channel.UNLIMITED)
        session.send(outChannel.consumeAsFlow().asFlux()).subscribe()
        try {
            session.receive().asFlow().collect { message ->
                when (message.type) {
                    TEXT -> handleTextMessage(session, message, outChannel)
                    BINARY -> handleBinaryMessage(session, message)
                    else -> {}
                }
            }
        } catch (e: Exception) {
            log.error("Error handling session: ${session.id}", e)
        }
        null
    }

    private suspend fun handleTextMessage(
        session: WebSocketSession,
        message: WebSocketMessage,
        output: Channel<WebSocketMessage>
    ) {
        val msg = message.payloadAsText
        log.info("Received message: $msg")

        if (REQUEST_END == msg) {
            log.info("Sending session: ${session.id}")
            val requestSession = sessions[session.id] ?: run {
                log.error("No session found for: ${session.id}")
                return
            }
            agentCaller.callAgent(
                requestSession.envelope.agentName,
                requestSession.envelope.payload,
                requestSession.data,
            ).flatMapConcat { (result, dataStream) ->
                flow {
                    val jsonResult = json.encodeToString(AgentResult.serializer(), result)
                    emit(session.textMessage(jsonResult))

                    dataStream?.let { stream ->
                        val bytes = stream.readAllBytes()
                        emit(session.binaryMessage { it.wrap(bytes) })
                    }
                }
            }.collect {
                log.debug("Sending message: $it")
                output.send(it)
                log.debug("Sent message: $it")
            }
            sessions.remove(session.id)
        } else {
            val envelope = json.decodeFromString(RequestEnvelope.serializer(), msg)
            log.info("Stored session: ${session.id}")
            sessions[session.id] = Session(session.id, envelope, WritableDataStream())
        }
    }

    private fun handleBinaryMessage(session: WebSocketSession, message: WebSocketMessage) {
        log.info("Received binary message for: ${session.id} ${sessions[session.id]}")
        sessions[session.id]?.let {
            val bytes = message.payload.asInputStream().use { b -> b.readAllBytes() }
            log.info("Received ${bytes.size} bytes")
            sessions[session.id]?.data?.write(bytes)
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
