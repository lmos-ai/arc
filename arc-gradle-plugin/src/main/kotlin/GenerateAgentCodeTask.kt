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

/**
 *
 */
open class GenerateAgentCodeTask : DefaultTask() {

    @get:InputDirectory
    lateinit var input: Directory

    @Internal // @get:OutputDirectory
    lateinit var output: Directory

    @TaskAction
    fun deploy() {
        println("Generating Agent code...")
        input.asFileTree.forEach {
            when {
                it.name.endsWith(".agent.kt") -> writeAgentsCode(it)
                it.name.endsWith(".functions.kt") -> writeFunctionsCode(it)
            }
            writeFunctionsCode(it)
        }
    }

    private fun writeAgentsCode(file: File) {
        val template = readTemplate("AgentTemplate.kt")
        var imports = ""
        var agents = ""
        var readingAgents = false
        file.readLines(charset = Charsets.UTF_8).forEach { line ->
            if (readingAgents) {
                agents += line + "\n"
            } else if (line.trim().startsWith("import ")) {
                imports += line + "\n"
            } else if (line.trim().startsWith("agent")) {
                readingAgents = true
                agents += line + "\n"
            }
        }
        File(output.asFile, ("Agents.kt")).writeText(
            template.replace("//@@IMPORTS@@", imports).replace("//@@AGENTS@@", agents)
        )
    }

    private fun writeFunctionsCode(file: File) {
        val template = readTemplate("FunctionTemplate.kt")
        var imports = ""
        var agents = ""
        var readingAgents = false
        file.readLines(charset = Charsets.UTF_8).forEach { line ->
            if (readingAgents) {
                agents += line + "\n"
            } else if (line.trim().startsWith("import ")) {
                imports += line + "\n"
            } else if (line.trim().startsWith("agent")) {
                readingAgents = true
                agents += line + "\n"
            }
        }
        File(output.asFile, ("Functions.kt")).writeText(
            template.replace("//@@IMPORTS@@", imports).replace("//@@FUNCTIONS@@", agents)
        )
    }

    private fun readTemplate(name: String): String {
        return Thread.currentThread().contextClassLoader.getResource(name)?.readText()!!
    }
}


