// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.client.ollama

data class OllamaClientConfig(
    val modelName: String,
    val url: String,
)
