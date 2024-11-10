// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.assistants.support.usecases

/**
 * Formats the given use cases to a string.
 *
 * The useAlternatives set is used to filter solutions.
 * Solutions with the headline "#### Alternative Solution" are filtered
 * unless the use case name is contained in the given set of alternatives.
 * In this case, the "primary" solution is filtered.
 */
fun List<UseCase>.formatToString(useAlternatives: Set<String>, useFallbacks: Set<String>) = buildString {
    this@formatToString.forEach { useCase ->
        val useAlternative = useAlternatives.contains(useCase.id) && useCase.alternativeSolution.isNotEmpty()
        val useFallback = useFallbacks.contains(useCase.id) && useCase.fallbackSolution.isNotEmpty()
        append(
            """
            |### UseCase: ${useCase.id}
            |${useCase.description}
            |
            """.trimMargin(),
        )
        if (useCase.steps.isNotEmpty()) {
            append("${useCase.steps}\n")
        }
        if (!useAlternative && !useFallback) {
            append("${useCase.solution}\n")
        }
        if (useAlternative && !useFallback) {
            append("${useCase.alternativeSolution}\n")
        }
        if (useFallback) {
            append("${useCase.fallbackSolution}\n")
        }
        if (useCase.examples.isNotEmpty()) {
            append("${useCase.examples}\n")
        }
        append("\n----\n\n")
    }
}
