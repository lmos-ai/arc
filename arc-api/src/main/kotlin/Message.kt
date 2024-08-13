package io.github.lmos.arc.api

import kotlinx.serialization.Serializable


@Serializable
data class Message(
    val role: String,
    val content: String,
    val format: String = "text",
    val turnId: String? = null,
)