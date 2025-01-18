// SPDX-FileCopyrightText: 2025 Deutsche Telekom AG and others
//
// SPDX-License-Identifier: Apache-2.0

package org.eclipse.lmos.arc.api

import kotlinx.serialization.Serializable

/**
 * Combines the agent name and the request into a single data type.
 */
@Serializable
data class RequestEnvelope(val agentName: String?, val payload: AgentRequest)
