// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.spring

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * Simple setup for loading configuration properties.
 */
@ConfigurationProperties(prefix = "arc.ai")
data class AIConfig(val clients: List<AIClientConfig>)

data class AIClientConfig(
    val id: String,
    val client: String,
    val modelName: String,
    val url: String? = null,
    val apiKey: String? = null,
)
