// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.api

import kotlinx.serialization.Serializable

/**
 * AgentResult
 */
@Serializable
data class AgentResult(
    val status: String? = null,
    val messages: List<Message>,
    val anonymizationEntities: List<AnonymizationEntity>? = null,
)
