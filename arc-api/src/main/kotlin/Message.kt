// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.api

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val role: String,
    val content: String,
    val format: String = "text",
    val turnId: String? = null,
    val binaryData: List<BinaryData>? = null,
)

@Serializable
class BinaryData(val mimeType: String, val data: String)
