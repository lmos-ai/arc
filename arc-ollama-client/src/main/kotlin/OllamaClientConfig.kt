// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package org.eclipse.lmos.arc.client.ollama

data class OllamaClientConfig(
    val modelName: String,
    val url: String?,
    val toolSupported: Boolean,
) {

    override fun toString(): String {
        return "OllamaClientConfig(modelName=$modelName, url=$url, toolSupported=$toolSupported)"
    }
}
