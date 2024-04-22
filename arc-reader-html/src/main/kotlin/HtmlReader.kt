// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.agents.dsl.extensions

import io.github.lmos.arc.agents.dsl.DSLContext
import io.github.lmos.arc.core.closeWith
import io.github.lmos.arc.core.failWith
import io.github.lmos.arc.core.result
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.net.URI

/**
 * Extensions for reading html files.
 * @param uriString The URI of the html file.
 */
fun DSLContext.htmlDocument(uriString: String) = result<Document, ReadHtmlException> {
    try {
        val inputStream = URI(uriString).toURL().openStream() closeWith { it.close() }
        Jsoup.parse(inputStream, null, uriString)
    } catch (ex: Exception) {
        failWith { ReadHtmlException(uriString, ex) }
    }
}

fun DSLContext.html(uriString: String) = result<String, ReadHtmlException> {
    val doc = htmlDocument(uriString) failWith { it }
    doc.text()
}

class ReadHtmlException(url: String, cause: Exception) : Exception("Failed to read html at $url", cause)
