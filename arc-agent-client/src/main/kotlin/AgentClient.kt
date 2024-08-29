// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.agent.client

import ai.ancf.lmos.arc.api.AgentRequest
import ai.ancf.lmos.arc.api.Message
import kotlinx.coroutines.flow.Flow

/**
 * Client for communicating with the Agents.
 */
interface AgentClient {

    /**
     * Calls the agent with the given request. If a url is not provided, the client will use the default url.
     */
    suspend fun callAgent(agentRequest: AgentRequest, url: String? = null): Flow<Message>
}

/**
 * Exception thrown when an error occurs during agent communication.
 */
class AgentException(message: String) : Exception(message)
