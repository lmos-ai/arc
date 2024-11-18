// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.scripting.agents

import ai.ancf.lmos.arc.agents.dsl.AgentDefinitionContext
import kotlin.script.experimental.annotations.KotlinScript
import kotlin.script.experimental.api.*
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.jvmTarget

/**
 * Configure Script Execution.
 */
@KotlinScript(
    fileExtension = "agent.kts",
    compilationConfiguration = ScriptConfiguration::class,
)
abstract class ScriptDeps

object ScriptConfiguration : ScriptCompilationConfiguration(
    {
        implicitReceivers(AgentDefinitionContext::class)

        compilerOptions("-Xcontext-receivers")

        defaultImports(
            "ai.ancf.lmos.arc.agents.dsl.get",
            "ai.ancf.lmos.arc.agents.dsl.*",
            "ai.ancf.lmos.arc.core.*",
            "ai.ancf.lmos.arc.agents.conversation.UserMessage",
            "ai.ancf.lmos.arc.agents.conversation.latest",
            "ai.ancf.lmos.arc.agents.dsl.extensions.*",
            "ai.ancf.lmos.arc.agents.llm.ChatCompletionSettings",
            "ai.ancf.lmos.arc.agents.llm.OutputFormat",
            "ai.ancf.lmos.arc.agents.dsl.custom.extensions.*",
        )

        ide {
            acceptedLocations(ScriptAcceptedLocation.Everywhere)
        }

        jvm {
            jvmTarget("21")
            dependenciesFromCurrentContext(wholeClasspath = true)
        }
    },
)
