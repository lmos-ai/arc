// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.agents.dsl.extensions

import ai.ancf.lmos.arc.agents.TestBase
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TemplateTest : TestBase() {

    @Test
    fun `test then extension`() {
        val predicate = true
        val result = """
           Hello${predicate then ", World!"}
       """.trim()
        assertThat(result).isEqualTo("Hello, World!")
    }
}
