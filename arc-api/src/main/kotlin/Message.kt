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

/**
 * Presents binary data in a message. The data can be encoded as base64 directly in this object or
 * this object may contain a source from where the data can be read, for example, a http address.
 */
@Serializable
class BinaryData(val mimeType: String, val dataAsBase64: String? = null, val source: String? = null)

/**
 * Indicates that the binary data is being streamed to the receiver server from the client.
 * This is used as a source in the [BinaryData] object.
 */
const val STREAM_SOURCE = "STREAM_SOURCE"
