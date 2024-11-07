// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.client.langchain4j

data class LangChainConfig(
    val modelName: String,
    val url: String?,
    val apiKey: String?,
    val accessKeyId: String?,
    val secretAccessKey: String?,
) {

    override fun toString(): String {
        return if (secretAccessKey != "***") copy(secretAccessKey = "***").toString() else super.toString()
    }
}
