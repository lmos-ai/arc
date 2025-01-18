// SPDX-FileCopyrightText: 2025 Deutsche Telekom AG and others
//
// SPDX-License-Identifier: Apache-2.0

package org.eclipse.lmos.arc.agents.conversation

import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class WritableDataStreamTest {

    @Test
    fun `test reading from WritableDataStream`(): Unit = runBlocking {
        val dataStream = WritableDataStream()
        dataStream.write("test".toByteArray(charset("UTF-8")))
        val output = dataStream.stream().first()
        assertThat(String(output, charset("UTF-8"))).isEqualTo("test")
    }

    @Test
    fun `test reading then writing WritableDataStream`(): Unit = runBlocking {
        val dataStream = WritableDataStream()
        var output = ""

        val job = async {
            dataStream.stream().collect {
                output = String(it, charset("UTF-8"))
            }
        }

        dataStream.write("test".toByteArray(charset("UTF-8")))
        dataStream.close()
        job.await()
        assertThat(output).isEqualTo("test")
    }

    @Test
    fun `test reading after closing WritableDataStream`(): Unit = runBlocking {
        val dataStream = WritableDataStream()
        var output = ""

        dataStream.write("test".toByteArray(charset("UTF-8")))
        dataStream.close()

        dataStream.stream().collect {
            output = String(it, charset("UTF-8"))
        }
        assertThat(output).isEqualTo("test")
    }
}
