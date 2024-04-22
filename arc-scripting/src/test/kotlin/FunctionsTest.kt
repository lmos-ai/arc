// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.scripting

import io.github.lmos.arc.agents.dsl.BasicFunctionDefinitionContext
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FunctionsTest : TestBase() {

    @Test
    fun `test script`(): Unit = runBlocking {
        val context = BasicFunctionDefinitionContext(testBeanProvider)
        functionEngine.eval(
            """
           function(
              name = "get_weather",
              description = "the weather service",
              params = types(string("location","the location"))
             ) {
                  "the weather is weather in location!"
              }
        """,
            context,
        )
        assertThat(context.functions).hasSize(1)
    }
}
