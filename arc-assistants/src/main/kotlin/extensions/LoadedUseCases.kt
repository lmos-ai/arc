package org.eclipse.lmos.arc.assistants.support.extensions

import org.eclipse.lmos.arc.assistants.support.usecases.UseCase

/**
 * Local variables stored by the Use Case extensions.
 */
data class LoadedUseCases(
    val useCases: List<UseCase>,
    val processedUseCases: String,
    val currentStep: String? = null,
    val currentUseCaseId: String? = null,
)
