// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.agents.dsl.extensions

import io.github.lmos.arc.agents.dsl.DSLContext
import io.github.lmos.arc.core.closeWith
import io.github.lmos.arc.core.failWith
import io.github.lmos.arc.core.result
import org.apache.pdfbox.Loader
import org.apache.pdfbox.text.PDFTextStripper
import java.net.URI

/**
 * Extensions for reading PDF files.
 * @param uriString The URI of the PDF file.
 */
fun DSLContext.pdf(uriString: String) = result<String, ReadPdfException> {
    try {
        val inputStream = URI(uriString).toURL().openStream() closeWith { it.close() }
        val document = Loader.loadPDF(inputStream.readAllBytes()) closeWith { it.close() }
        PDFTextStripper().getText(document)
    } catch (ex: Exception) {
        failWith { ReadPdfException(uriString, ex) }
    }
}

class ReadPdfException(url: String, cause: Exception) : Exception("Failed to read PDF at $url", cause)
