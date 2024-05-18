// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0
package io.github.lmos.arc.scripting.functions

import io.github.lmos.arc.scripting.TestBase
import io.github.lmos.arc.scripting.readScript
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File

class ScriptingLLMFunctionLoaderTest : TestBase() {

    @Test
    fun `test loading scripting functions from file`(): Unit = runBlocking {
        val scriptFile = File(scripts, "weather.functions.kts").also {
            it.writeText(readScript("weather.functions.kts"))
        }
        scriptingLLMFunctionLoader.loadFunctions(scriptFile)
        assertThat(scriptingLLMFunctionLoader.load()).hasSize(2)
    }
}