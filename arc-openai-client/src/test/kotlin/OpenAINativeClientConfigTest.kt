// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.client.openai

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class OpenAINativeClientConfigTest {

    @Test
    fun testToString() {
        val config = OpenAINativeClientConfig(
            modelName = "test-model",
            url = "http://test-url",
            apiKey = "test-api-key",
        )
        val expected = "OpenAINativeClientConfig(modelName=test-model, url=http://test-url, apiKey=***)"
        assertEquals(expected, config.toString())
    }
}
