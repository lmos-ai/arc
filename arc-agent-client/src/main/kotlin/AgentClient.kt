// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.agent.client

import io.github.lmos.arc.api.AgentRequest
import io.github.lmos.arc.api.Message
import kotlinx.coroutines.flow.Flow

/**
 * Client for communicating with the Agents.
 */
interface AgentClient {

    suspend fun callAgent(agentRequest: AgentRequest): Flow<Message>
}

/**
 * Exception thrown when an error occurs during agent communication.
 */
class AgentException(message: String) : Exception(message)
