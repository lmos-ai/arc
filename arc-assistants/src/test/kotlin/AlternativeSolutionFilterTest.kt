// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.assistants.support

import ai.ancf.lmos.arc.agents.dsl.extensions.local
import ai.ancf.lmos.arc.assistants.support.usecases.formatToString
import ai.ancf.lmos.arc.assistants.support.usecases.toUseCases
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AlternativeSolutionFilterTest : TestBase() {

    @Test
    fun `test alternative solution filter`(): Unit = runBlocking {
        val useCases = local("use_cases.md")!!.toUseCases()
        val parsedUseCases = useCases.formatToString(emptySet())
        assertThat(parsedUseCases).doesNotContain("alternative")
        assertThat(parsedUseCases).contains("usecase1", "usecase2", "usecase3")
    }

    @Test
    fun `test alternative solution filter with alternatives`(): Unit = runBlocking {
        val useCases = local("use_cases.md")!!.toUseCases()
        val parsedUseCases = useCases.formatToString(setOf("usecase2"))
        assertThat(parsedUseCases).doesNotContain("Primary Solution")
        assertThat(parsedUseCases).contains("Alternative solution")
    }
}
