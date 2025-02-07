// SPDX-FileCopyrightText: 2025 Deutsche Telekom AG and others
//
// SPDX-License-Identifier: Apache-2.0

package org.eclipse.lmos.arc.assistants.support.usecases

import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.eclipse.lmos.arc.agents.dsl.extensions.local
import org.eclipse.lmos.arc.agents.dsl.withDSLContext
import org.junit.jupiter.api.Test

class VersionExtractorTest {

    @Test
    fun `test use case version id extraction from use cases`(): Unit = runBlocking {
        withDSLContext {
            val useCases = local("use_cases.md")!!.toUseCases()
            assertThat(useCases[1].version).isEqualTo("1.0.0")
        }
    }

    @Test
    fun `test use case version id extraction`() {
        val version = extractVersion("This is a use case <Version:1.0.0>")
        assertThat(version).isEqualTo("1.0.0")
    }

    @Test
    fun `test use case version id extraction - case sensitive`() {
        val version = extractVersion("This is a use case <verSion:1.0.0>")
        assertThat(version).isEqualTo("1.0.0")
    }

    @Test
    fun `test use case version id extraction - empty strings`() {
        val version = extractVersion("This is a use case <Version: 1.0.0 >")
        assertThat(version).isEqualTo("1.0.0")
    }
}
