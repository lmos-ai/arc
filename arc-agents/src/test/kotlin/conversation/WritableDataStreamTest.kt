// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.agents.conversation

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class WritableDataStreamTest {

    @Test
    fun `test reading from WritableDataStream`(): Unit = runBlocking {
        val dataStream = WritableDataStream()
        var output = ""

        dataStream.write("test".toByteArray(charset("UTF-8")))
        dataStream.stream().collect {
            output = String(it, charset("UTF-8"))
        }
        assertThat(output).isEqualTo("test")
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
