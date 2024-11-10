// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.assistants.support

import ai.ancf.lmos.arc.assistants.support.usecases.extractUseCaseId
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class UseCaseIdExtractorTest : TestBase() {

    @Test
    fun `test alternative solution filter`(): Unit = runBlocking {
        val message = """<ID:use_case01>
                 This is a reply from the LLM."""
        val (filteredMessage, useCaseId) = extractUseCaseId(message)
        assertThat(filteredMessage).contains("This is a reply from the LLM.")
        assertThat(useCaseId).contains("use_case01")
    }
}
