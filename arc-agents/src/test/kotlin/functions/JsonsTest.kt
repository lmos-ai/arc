// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.agents.functions

import ai.ancf.lmos.arc.agents.TestBase
import ai.ancf.lmos.arc.core.Failure
import ai.ancf.lmos.arc.core.getOrThrow
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class JsonsTest : TestBase() {

    @Test
    fun `test convertToJsonMap`() {
        val jsonMap = """
            { 
              "name": "Bard", 
              "array": [1,2],
              "number": 3,
              "object": { "key": "value" }
             }
            """.convertToJsonMap().getOrThrow()
        assertThat(jsonMap["name"]).isEqualTo("Bard")
        assertThat(jsonMap["array"] as List<Int>).contains(1, 2)
        assertThat(jsonMap["number"] as Int).isEqualTo(3)
        assertThat(jsonMap["object"] as Map<String, Any?>).containsEntry("key", "value")
    }

    @Test
    fun `test convertToJsonMap fails`() {
        val result = """
            { 
              "name": "B
            """.convertToJsonMap()
        assertThat(result).isInstanceOf(Failure::class.java)
        assertThat((result as Failure).reason).isInstanceOf(InvalidJsonException::class.java)
    }
}
