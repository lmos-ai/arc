// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.api

import kotlinx.serialization.Serializable

/**
 * Combines the agent name and the request into a single data type.
 */
@Serializable
data class RequestEnvelope(val agentName: String?, val payload: AgentRequest)