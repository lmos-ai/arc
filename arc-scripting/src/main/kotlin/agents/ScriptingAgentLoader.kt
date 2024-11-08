// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.scripting.agents

import ai.ancf.lmos.arc.agents.Agent
import ai.ancf.lmos.arc.agents.AgentLoader
import ai.ancf.lmos.arc.agents.dsl.AgentFactory
import ai.ancf.lmos.arc.agents.dsl.BasicAgentDefinitionContext
import ai.ancf.lmos.arc.agents.events.EventPublisher
import ai.ancf.lmos.arc.core.Failure
import ai.ancf.lmos.arc.core.Result
import ai.ancf.lmos.arc.core.Success
import ai.ancf.lmos.arc.core.onFailure
import ai.ancf.lmos.arc.scripting.ScriptFailedException
import org.slf4j.LoggerFactory
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import kotlin.script.experimental.api.ResultValue

/**
 * Provider for agents defined in Agent/Function scripts.
 * This provider has a hot-reload feature that will reload agents from the given folder in short intervals.
 */
class ScriptingAgentLoader(
    private val agentFactory: AgentFactory<*>,
    private val agentScriptEngine: AgentScriptEngine,
    private val eventPublisher: EventPublisher? = null,
) : AgentLoader {

    private val log = LoggerFactory.getLogger(this.javaClass)
    private val agents = ConcurrentHashMap<String, Agent<*, *>>()

    override fun getAgents() = agents.values.toList()

    fun getAgentByName(name: String) = agents[name]

    /**
     * Loads the agents defined in an Agent DSL script.
     */
    fun loadAgent(agentScript: String): Result<ResultValue?, ScriptFailedException> {
        val context = BasicAgentDefinitionContext(agentFactory)
        val result = agentScriptEngine.eval(agentScript, context)
        if (result is Success && context.agents.isNotEmpty()) {
            log.info("Discovered the following agents (scripting): ${context.agents.joinToString { it.name }}")
            agents.putAll(context.agents.associateBy { it.name })
        }
        return result
    }

    fun loadCompiledAgent(compiledAgentScript: CompiledAgentLoader) {
        val context = BasicAgentDefinitionContext(agentFactory)
        compiledAgentScript.load(context)
        if (context.agents.isNotEmpty()) {
            log.info("Discovered the following agents (scripting): ${context.agents.joinToString { it.name }}")
            agents.putAll(context.agents.associateBy { it.name })
        }
    }

    /**
     * Loads the agents defined in a list of Agent DSL script files.
     */
    fun loadAgents(vararg files: File) {
        files
            .asSequence()
            .filter { it.name.endsWith(".agent.kts") }
            .map { it.name to it.readText() }
            .forEach { (name, script) ->
                val result = loadAgent(script).onFailure {
                    log.warn("Failed to load agents from script: $name!", it)
                }
                when (result) {
                    is Success -> eventPublisher?.publish(AgentLoadedEvent(name))
                    is Failure -> eventPublisher?.publish(
                        AgentLoadedEvent(
                            name,
                            result.reason.message ?: "Unknown error!",
                        ),
                    )
                }
            }
    }
}
