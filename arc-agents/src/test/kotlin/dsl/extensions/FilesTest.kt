// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.agents.dsl.extensions

import io.github.lmos.arc.agents.TestBase
import io.github.lmos.arc.agents.dsl.BasicDSLContext
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class FilesTest : TestBase() {

    @Test
    fun `test read file from classpath`(): Unit = runBlocking {
        val context = BasicDSLContext(testBeanProvider)
        val result = context.local("test.txt")
        assertThat(result).contains("ARC is fun!")
    }

    @Test
    fun `test return null when file missing`(): Unit = runBlocking {
        val context = BasicDSLContext(testBeanProvider)
        val result = context.local("test_t.txt")
        assertThat(result).isNull()
    }
}
