// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.assistants.support.usecases

import ai.ancf.lmos.arc.assistants.support.usecases.Section.*

/**
 * Parses the given string into a list of use cases.
 */
fun String.toUseCases(): List<UseCase> {
    val useCases = mutableListOf<UseCase>()
    var currentUseCase: UseCase? = null
    var currentSection = NONE

    forEachLine { line ->
        if (line.startsWith("#")) {
            if (line.contains("# UseCase")) {
                currentUseCase?.let { useCases.add(it) }
                currentUseCase = UseCase(id = line.substringAfter(":").trim())
                currentSection = NONE
            } else {
                currentSection = when {
                    line.contains("# Description") -> DESCRIPTION
                    line.contains("# Solution") -> SOLUTION
                    line.contains("# Alternative") -> ALTERNATIVE_SOLUTION
                    line.contains("# Fallback") -> FALLBACK_SOLUTION
                    line.contains("# Step") -> STEPS
                    line.contains("# Example") -> EXAMPLES
                    else -> error("Unknown UseCase section: $line")
                }
            }
        } else if (line.startsWith("----")) {
            currentSection = NONE
        }
        currentUseCase = when (currentSection) {
            SOLUTION -> currentUseCase?.copy(solution = (currentUseCase?.solution ?: "") + line)
            STEPS -> currentUseCase?.copy(steps = (currentUseCase?.steps ?: "") + line)
            EXAMPLES -> currentUseCase?.copy(examples = (currentUseCase?.examples ?: "") + line)
            DESCRIPTION -> currentUseCase?.copy(
                description = (currentUseCase?.description ?: "") + line,
            )

            FALLBACK_SOLUTION -> currentUseCase?.copy(
                fallbackSolution = (currentUseCase?.fallbackSolution ?: "") + line,
            )

            ALTERNATIVE_SOLUTION -> currentUseCase?.copy(
                alternativeSolution = (
                    currentUseCase?.alternativeSolution
                        ?: ""
                    ) + line.replace("# Alternative Solution", "# Solution"),
            )

            NONE -> currentUseCase
        }
    }
    currentUseCase?.let { useCases.add(it) }
    return useCases
}

/**
 * Splits a given string into a list of lines.
 */
private inline fun String.forEachLine(crossinline fn: (String) -> Unit) {
    return split("\n").forEach { fn(it + "\n") }
}

enum class Section {
    NONE,
    DESCRIPTION,
    SOLUTION,
    ALTERNATIVE_SOLUTION,
    FALLBACK_SOLUTION,
    STEPS,
    EXAMPLES,
}

data class UseCase(
    val id: String,
    val description: String = "",
    val steps: String = "",
    val solution: String = "",
    val alternativeSolution: String = "",
    val fallbackSolution: String = "",
    val examples: String = "",
)
