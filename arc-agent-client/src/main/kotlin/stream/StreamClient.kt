// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package org.eclipse.lmos.arc.agent.client.stream

import org.eclipse.lmos.arc.api.AgentRequest
import org.eclipse.lmos.arc.api.AgentResult
import org.eclipse.lmos.arc.api.REQUEST_END
import org.eclipse.lmos.arc.api.RequestEnvelope
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.io.Closeable
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Implementation of [AgentClient] that uses the Arc Streaming Endpoint to communicate with agents.
 */
class StreamClient : Closeable {

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

    fun callAgent(
        agentRequest: AgentRequest,
        agentName: String? = null,
        url: String? = null,
        dataStream: DataStream? = null,
        requestHeaders: Map<String, Any> = emptyMap(),
    ) = flow {
        client.webSocket(url!!) {
            headers { requestHeaders.forEach { (key, value) -> append(key, value.toString()) } }
            val request = RequestEnvelope(agentName, agentRequest)

            send(Frame.Text(json.encodeToString(request)))
            dataStream?.read()?.collect { data -> send(Frame.Binary(false, data)) }
            send(Frame.Text(REQUEST_END))

            while (closing.get().not()) {
                val next = nextMessage()
                emit(next)
            }
            close()
        }
    }

    private suspend fun DefaultClientWebSocketSession.nextMessage(): AgentResult {
        val response = incoming.receive() as Frame.Text
        return response.readText().let {
            log.trace("Received $it")
            json.decodeFromString(it)
        }
    }

    override fun close() {
        closing.set(true)
        client.close()
    }
}
