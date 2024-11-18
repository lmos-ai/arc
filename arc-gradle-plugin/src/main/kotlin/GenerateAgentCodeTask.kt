// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.gradle.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import java.io.File
import kotlin.text.Charsets.UTF_8

/**
 * Converts agent and function scripts into Kotlin code.
 */
open class GenerateAgentCodeTask : DefaultTask() {

    @get:InputDirectory
    lateinit var input: Directory

    @Internal // @get:OutputDirectory
    lateinit var output: Directory

    @TaskAction
    fun deploy() {
        println("Generating Agent code...")

        val agentCode = Code(setOf(), "")
        input.asFileTree.filter {
            it.name.endsWith(".agent.kts")
        }.forEach { writeCode(it, agentCode, "agent") }
        write("Agents.kt", agentCode, "AgentTemplate.kt")

        val functionCode = Code(setOf(), "")
        input.asFileTree.filter {
            it.name.endsWith(".functions.kts")
        }.forEach { writeCode(it, functionCode, "function") }
        write("Functions.kt", functionCode, "FunctionTemplate.kt")
    }

    private fun write(name: String, code: Code, template: String) {
        File(output.asFile, name).writeText(
            readTemplate(template)
                .replace("//@@IMPORTS@@", code.imports.joinToString(""))
                .replace("//@@CODE@@", code.code)
        )
    }

    private fun writeCode(file: File, code: Code, codeStart: String) {
        var readingCode = false
        file.readLines(charset = UTF_8).forEach { line ->
            if (readingCode) {
                code.code += line + "\n"
            } else if (line.trim().startsWith("import ")) {
                code.imports += line + "\n"
            } else if (line.trim().startsWith(codeStart)) {
                readingCode = true
                code.code += line + "\n"
            }
        }
    }

    private fun readTemplate(name: String): String {
        return Thread.currentThread().contextClassLoader.getResource(name)?.readText()!!
    }
}

data class Code(var imports: Set<String>, var code: String)


