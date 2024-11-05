// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

import ai.ancf.lmos.arc.agents.dsl.BasicDSLContext
import ai.ancf.lmos.arc.agents.dsl.BeanProvider
import ai.ancf.lmos.arc.agents.dsl.extensions.htmlDocument
import ai.ancf.lmos.arc.core.getOrThrow
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.reflect.KClass

class HtmlReaderTest {

    @Test
    fun `test reading a html file`(): Unit = runBlocking {
        val result = File("src/test/resources/test.html").readText().htmlDocument().getOrThrow()
        assertThat(result.text().trim()).isEqualTo("This is a test!")
    }

    @Test
    fun `test reading a html document`(): Unit = runBlocking {
        val result = File("src/test/resources/test.html").readText().htmlDocument().getOrThrow()
        assertThat(result.select("p").text()).isEqualTo("test!")
    }

    private fun dslContext() = BasicDSLContext(object : BeanProvider {
        override suspend fun <T : Any> provide(bean: KClass<T>): T {
            error("err")
        }
    })
}
