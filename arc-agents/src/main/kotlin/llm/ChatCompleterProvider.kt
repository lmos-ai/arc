// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.agents.llm

/**
 * Provides ChatCompleter based on their model name.
 * ChatCompleterProviders must provide a default ChatCompleter if no model is explicitly defined.
 */
fun interface ChatCompleterProvider {

    fun provideByModel(model: String?): ChatCompleter
}
