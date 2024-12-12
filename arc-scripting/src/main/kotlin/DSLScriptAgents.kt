// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.scripting

import ai.ancf.lmos.arc.agents.Agent
import ai.ancf.lmos.arc.agents.AgentProvider
import ai.ancf.lmos.arc.agents.CompositeAgentProvider
import ai.ancf.lmos.arc.agents.dsl.ChatAgentFactory
import ai.ancf.lmos.arc.agents.dsl.CompositeBeanProvider
import ai.ancf.lmos.arc.agents.dsl.beans
import ai.ancf.lmos.arc.agents.events.BasicEventPublisher
import ai.ancf.lmos.arc.agents.events.Event
import ai.ancf.lmos.arc.agents.events.EventHandler
import ai.ancf.lmos.arc.agents.events.LoggingEventHandler
import ai.ancf.lmos.arc.agents.functions.CompositeLLMFunctionProvider
import ai.ancf.lmos.arc.agents.functions.LLMFunctionProvider
import ai.ancf.lmos.arc.agents.llm.ChatCompleterProvider
import ai.ancf.lmos.arc.core.failWith
import ai.ancf.lmos.arc.core.result
import ai.ancf.lmos.arc.scripting.agents.ScriptingAgentLoader
import ai.ancf.lmos.arc.scripting.functions.ScriptingLLMFunctionLoader

/**
 * A convenience class for setting up the agent system and
 * defining agents and functions in a DSL.
 */
class DSLScriptAgents private constructor(
    private val functionsLoader: ScriptingLLMFunctionLoader,
    private val agentLoader: ScriptingAgentLoader,
    private val agentProvider: AgentProvider,
    private val functionProvider: LLMFunctionProvider,
) : AgentProvider, LLMFunctionProvider {
    companion object {

        fun init(
            chatCompleterProvider: ChatCompleterProvider,
            beans: Set<Any> = emptySet(),
            handlers: List<EventHandler<out Event>> = emptyList(),
        ): DSLScriptAgents {
            /**
             * Set up the event system.
             */
            val eventPublisher = BasicEventPublisher(LoggingEventHandler(), *handlers.toTypedArray())

            /**
             * Set up the bean provider.
             */
            val beanProvider = beans(chatCompleterProvider, eventPublisher, *beans.toTypedArray())

            /**
             * Set up the loading of agent functions from scripts.
             */
            val functionLoader = ScriptingLLMFunctionLoader(beanProvider, eventPublisher = eventPublisher)
            val functionProvider = CompositeLLMFunctionProvider(listOf(functionLoader))

            /**
             * Set up the loading of agents from scripts.
             */
            val agentFactory = ChatAgentFactory(CompositeBeanProvider(setOf(functionProvider), beanProvider))
            val agentLoader = ScriptingAgentLoader(agentFactory, eventPublisher = eventPublisher)
            val agentProvider = CompositeAgentProvider(listOf(agentLoader), emptyList())

            return DSLScriptAgents(functionLoader, agentLoader, agentProvider, functionProvider)
        }
    }

    /**
     * Define agents.
     */
    fun define(agentDSLScript: String) = result<Int, ScriptFailedException> {
        agentLoader.loadAgent(agentDSLScript) failWith { it }
        agentLoader.getAgents().size
    }

    /**
     * Define functions.
     */
    fun defineFunctions(functionDSLScript: String) = result<Int, ScriptFailedException> {
        functionsLoader.loadFunction(functionDSLScript) failWith { it }
        functionsLoader.load().size
    }

    /**
     * Get agents.
     */
    override fun getAgents(): List<Agent<*, *>> = agentProvider.getAgents()

    /**
     * Get functions.
     */
    override fun provide(functionName: String) = functionProvider.provide(functionName)

    override fun provideAll() = functionProvider.provideAll()
}
