package io.github.lmos.arc.api

import kotlinx.serialization.Serializable

@Serializable
data class AnonymizationEntity(val type: String, val value: String, val replacement: String)
