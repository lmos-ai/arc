// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.runner

import picocli.CommandLine.Command
import picocli.CommandLine.Parameters

@Command(
    name = "spring",
    mixinStandardHelpOptions = true,
    description = ["Creates a new, empty spring boot host project."],
)
open class Spring : Runnable {

    @Parameters(
        index = "0",
        description = ["The name of the project."],
    )
    private var name: String = ""

    override fun run() {
        if (name.isEmpty()) {
            println("Invalid agent name. Please provide a valid agent name.")
        }
        ProcessBuilder("git", "clone", "https://github.com/lmos-ai/arc-spring-init", name)
            .start()
            .apply {
                inputStream.bufferedReader().use { reader ->
                    reader.lines().forEach { println(it) }
                }
            }
            .waitFor()
        println("Run '$name/gradlew bootRun' to start the server.")
    }
}
