package io.github.lmos.arc.api

import kotlinx.serialization.Serializable

/**
 * AgentResult
 */
@Serializable
data class AgentResult(
    val messages: List<Message>,
    val anonymizationEntities: List<AnonymizationEntity>? = null,
)
