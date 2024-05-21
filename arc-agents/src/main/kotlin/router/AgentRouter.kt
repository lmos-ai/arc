// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.agents.router

import io.github.lmos.arc.agents.Agent
import io.github.lmos.arc.agents.AgentProvider
import io.github.lmos.arc.agents.getAgentByName
import io.github.lmos.arc.agents.llm.TextEmbedder
import io.github.lmos.arc.agents.llm.TextEmbeddings
import io.github.lmos.arc.agents.llm.embed
import io.github.lmos.arc.core.getOrThrow
import java.util.concurrent.atomic.AtomicReference

/**
 * AgentRouters find the Agent that is best suited to handle an incoming prompt/utterance from a client.
 */
interface AgentRouter {
    suspend fun addRoute(name: String, routes: List<String>)
    suspend fun route(prompt: String): Agent<*, *>?
}

/**
 * AgentRouter that uses a semantic similarity to find Agents.
 */
class SemanticAgentRouter(
    private val textEmbedder: TextEmbedder,
    private val agentProvider: AgentProvider
) : AgentRouter {

    private val allRoutes = AtomicReference(TextEmbeddings(emptyList()))

    override suspend fun addRoute(name: String, routes: List<String>) {
        val embeddings = textEmbedder.embed(routes).getOrThrow().embeddings.map { it.copy(labels = setOf(name)) }
        allRoutes.updateAndGet { TextEmbeddings(it.embeddings + embeddings) }
    }

    override suspend fun route(prompt: String): Agent<*, *>? {
        val embedding = allRoutes.get().findClosest(textEmbedder.embed(prompt).getOrThrow()).first
        // log
        return agentProvider.getAgentByName(embedding.labels.first())
    }
}