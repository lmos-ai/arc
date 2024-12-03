// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.runner.server

import ai.ancf.lmos.arc.agents.AgentProvider
import ai.ancf.lmos.arc.agents.CompositeAgentProvider
import ai.ancf.lmos.arc.agents.dsl.ChatAgentFactory
import ai.ancf.lmos.arc.agents.dsl.CompositeBeanProvider
import ai.ancf.lmos.arc.agents.dsl.SetBeanProvider
import ai.ancf.lmos.arc.agents.events.BasicEventPublisher
import ai.ancf.lmos.arc.agents.events.LoggingEventHandler
import ai.ancf.lmos.arc.agents.functions.CompositeLLMFunctionProvider
import ai.ancf.lmos.arc.graphql.inbound.EventSubscriptionHolder
import ai.ancf.lmos.arc.scripting.ScriptHotReload
import ai.ancf.lmos.arc.scripting.agents.ScriptingAgentLoader
import ai.ancf.lmos.arc.scripting.functions.ScriptingLLMFunctionLoader
import kotlin.time.Duration.Companion.seconds

/**
 * Initializes the ARC Framework.
 */
fun setupArc(appConfig: AppConfig): Pair<AgentProvider, EventSubscriptionHolder> {
    /**
     * Set up the event system.
     */
    val eventSubscriptionHolder = EventSubscriptionHolder()
    val eventPublisher = BasicEventPublisher(LoggingEventHandler(), eventSubscriptionHolder)

    /**
     * Set up the chat completer and the bean provider.
     */
    val chatCompleterProvider = chatCompleterProvider(appConfig.clientConfig, eventPublisher)
    val beanProvider = SetBeanProvider(setOf(chatCompleterProvider, eventPublisher))

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
    agentLoader.loadAgentsFromFolder(appConfig.scriptFolder)

    /**
     * Set up hot-reload for agents and functions.
     */
    val scriptHotReload = ScriptHotReload(agentLoader, functionLoader, 3.seconds)
    scriptHotReload.start(appConfig.scriptFolder)

    return CompositeAgentProvider(listOf(agentLoader), emptyList()) to eventSubscriptionHolder
}
