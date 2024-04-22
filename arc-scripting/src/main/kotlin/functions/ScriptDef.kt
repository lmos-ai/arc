// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.scripting.functions

import io.github.lmos.arc.agents.dsl.FunctionDefinitionContext
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.defaultImports
import kotlin.script.experimental.api.implicitReceivers
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.jvmTarget

/**
 * Configure Script Execution.
 */
@KotlinScript(
    fileExtension = "functions.kts",
    compilationConfiguration = ScriptConfiguration::class,
)
abstract class ScriptDeps

object ScriptConfiguration : ScriptCompilationConfiguration(
    {
        implicitReceivers(FunctionDefinitionContext::class)

        defaultImports(
            "io.github.lmos.arc.agents.dsl.get",
            "io.github.lmos.arc.agents.dsl.*",
            "io.github.lmos.arc.agents.dsl.extensions.*",
        )

        jvm {
            jvmTarget("17")
            dependenciesFromCurrentContext(wholeClasspath = true)
        }
    },
)
