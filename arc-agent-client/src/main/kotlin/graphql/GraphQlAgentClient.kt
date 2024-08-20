// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.agent.client.graphql

import io.github.lmos.arc.agent.client.AgentClient
import io.github.lmos.arc.agent.client.AgentException
import io.github.lmos.arc.api.AgentRequest
import io.github.lmos.arc.api.Message
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Implementation of [AgentClient] that uses GraphQL over WebSockets to communicate with the agents.
 */
class GraphQlAgentClient(private val defaultUrl: String? = null) : AgentClient, Closeable {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val closing = AtomicBoolean(false)
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val client = HttpClient(CIO) {
        install(WebSockets) {
            pingInterval = 20_000
        }
    }

    override suspend fun callAgent(agentRequest: AgentRequest, url: String?) = flow {
        if (url == null && defaultUrl == null) error("Agent Url not provided!")
        val opId = UUID.randomUUID().toString()
        client.webSocket(url ?: defaultUrl!!) {
            initConnection()
            sendSubscription(opId, agentRequest)
            while (closing.get().not()) {
                when (val next = nextMessage()) {
                    is NextMessage -> next.payload.data.agent.messages.forEach { message ->
                        emit(Message(content = message.content, role = "assistant"))
                    }

                    is CompleteMessage -> break
                    is ErrorMessage -> throw AgentException("Error received for $opId! Error:[$next]")
                    else -> {}
                }
            }
        }
        client.close()
    }

    private suspend fun DefaultClientWebSocketSession.initConnection() {
        sendMessage(InitConnectionMessage)
        val response = nextMessage()
        if (response !is AckConnectionMessage) {
            throw AgentException("Connection not acknowledged! Received $response")
        }
    }

    private suspend fun DefaultClientWebSocketSession.sendSubscription(opId: String, agentRequest: AgentRequest) {
        sendMessage(SubscribeMessage(opId, ClientPayload(AGENT_SUBSCRIPTION, AgentRequestVariables(agentRequest))))
    }

    private suspend fun DefaultClientWebSocketSession.sendMessage(message: ClientMessage) {
        val jsonCall = json.encodeToString(message)
        log.debug("Sending $jsonCall")
        send(Frame.Text(jsonCall))
    }

    private suspend fun DefaultClientWebSocketSession.nextMessage(): ServerMessage {
        val response = incoming.receive() as Frame.Text
        return response.readText().let {
            log.debug("Received $it")
            json.decodeFromString(it)
        }
    }

    override fun close() {
        closing.set(true)
        client.close()
    }
}
