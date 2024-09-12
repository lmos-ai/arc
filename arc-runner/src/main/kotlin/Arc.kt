@file:JvmName("RunKt") // SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.runner

import picocli.CommandLine.Command
import java.io.File

@Command(
    name = "arc",
    mixinStandardHelpOptions = true,
    version = ["1.0.0"],
    subcommands = [RunArc::class, ListAgents::class, InstallAgent::class],
    description = ["The Arc CLI."],
)
class Arc

fun home(): File {
    val home = File(System.getProperty("user.home"), ".arc")
    home.mkdirs()
    return home
}
