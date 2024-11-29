// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.client.langchain4j

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LangChainClientTest {

    private val langChainConfig = LangChainConfig(
        modelName = "test-model",
        url = "http://test-url",
        apiKey = "test-api-key",
        accessKeyId = "test-access-key-id",
        secretAccessKey = "test-secret-access-key",
    )

    @Test
    fun testToString() {
        val expected = "LangChainConfig(modelName=test-model, url=http://test-url, apiKey=***, accessKeyId=test-access-key-id, secretAccessKey=***)"
        assertEquals(expected, langChainConfig.toString())
    }
}
