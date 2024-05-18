// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package io.github.lmos.arc.spring

import io.github.lmos.arc.agents.Agent
import io.github.lmos.arc.agents.AgentLoader
import io.github.lmos.arc.agents.AgentProvider
import io.github.lmos.arc.agents.CompositeAgentProvider
import io.github.lmos.arc.agents.dsl.AgentFactory
import io.github.lmos.arc.agents.dsl.BeanProvider
import io.github.lmos.arc.agents.dsl.ChatAgentFactory
import io.github.lmos.arc.agents.dsl.CoroutineBeanProvider
import io.github.lmos.arc.agents.events.BasicEventPublisher
import io.github.lmos.arc.agents.events.EventHandler
import io.github.lmos.arc.agents.events.LoggingEventHandler
import io.github.lmos.arc.agents.events.addAll
import io.github.lmos.arc.agents.functions.CompositeLLMFunctionProvider
import io.github.lmos.arc.agents.functions.LLMFunction
import io.github.lmos.arc.agents.functions.LLMFunctionLoader
import io.github.lmos.arc.agents.functions.LLMFunctionProvider
import io.github.lmos.arc.agents.memory.InMemoryMemory
import io.github.lmos.arc.agents.memory.Memory
import io.github.lmos.arc.scripting.ScriptHotReload
import io.github.lmos.arc.scripting.agents.AgentScriptEngine
import io.github.lmos.arc.scripting.agents.CompiledAgentLoader
import io.github.lmos.arc.scripting.agents.KtsAgentScriptEngine
import io.github.lmos.arc.scripting.agents.ScriptingAgentLoader
import io.github.lmos.arc.scripting.functions.FunctionScriptEngine
import io.github.lmos.arc.scripting.functions.KtsFunctionScriptEngine
import io.github.lmos.arc.scripting.functions.ScriptingLLMFunctionLoader
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import java.io.File
import java.time.Duration
import kotlin.reflect.KClass
import kotlin.time.toKotlinDuration

@AutoConfiguration
@Import(MetricConfiguration::class)
class ArcAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(BeanProvider::class)
    fun beanProvider(beanFactory: ConfigurableBeanFactory): BeanProvider = CoroutineBeanProvider(object : BeanProvider {
        override suspend fun <T : Any> provide(bean: KClass<T>) = beanFactory.getBean(bean.java)
    })

    @Bean
    fun eventPublisher(eventHandlers: List<EventHandler<*>>) = BasicEventPublisher().apply {
        addAll(eventHandlers)
    }

    @Bean
    @ConditionalOnMissingBean(AgentFactory::class)
    fun agentFactory(beanProvider: BeanProvider) = ChatAgentFactory(beanProvider)

    @Bean
    fun functionScriptEngine(): FunctionScriptEngine = KtsFunctionScriptEngine()

    @Bean
    fun agentScriptEngine(): AgentScriptEngine = KtsAgentScriptEngine()

    @Bean
    @ConditionalOnMissingBean(Memory::class)
    fun memory() = InMemoryMemory()

    @Bean
    fun loggingEventHandler() = LoggingEventHandler()

    @Bean
    @ConditionalOnProperty("arc.scripts.hotReload.enable", havingValue = "true")
    fun scriptHotReload(
        @Value("\${arc.scripts.folder:/agents}") agentsFolder: File,
        @Value("\${arc.scripts.hotReload.delay:PT3M}") hotReloadDelay: Duration,
        agentLoader: ScriptingAgentLoader,
        functionLoader: ScriptingLLMFunctionLoader,
    ): ScriptHotReload {
        if (!agentsFolder.exists()) error("Agents folder does not exist: $agentsFolder!")
        return ScriptHotReload(agentLoader, functionLoader, hotReloadDelay.toKotlinDuration())
    }

    @Bean
    @ConditionalOnMissingBean(AgentProvider::class)
    fun agentProvider(loaders: List<AgentLoader>, agents: List<Agent<*, *>>): AgentProvider =
        CompositeAgentProvider(loaders, agents)

    @Bean
    fun scriptingAgentLoader(
        agentScriptEngine: AgentScriptEngine,
        agentFactory: AgentFactory<*>,
        compiledAgents: List<CompiledAgentLoader>?,
        @Value("\${arc.scripts.folder:/agents}") agentsFolder: File,
    ): ScriptingAgentLoader {
        return ScriptingAgentLoader(agentFactory, agentScriptEngine).also { loader ->
            compiledAgents?.forEach(loader::loadCompiledAgent)
            if (agentsFolder.exists()) agentsFolder.listFiles()?.let { loader.loadAgents(*it) }
        }
    }

    @Bean
    fun llmFunctionProvider(loaders: List<LLMFunctionLoader>, functions: List<LLMFunction>): LLMFunctionProvider =
        CompositeLLMFunctionProvider(loaders, functions)

    @Bean
    fun scriptingLLMFunctionProvider(
        functionScriptEngine: FunctionScriptEngine,
        beanProvider: BeanProvider,
        @Value("\${arc.scripts.folder:/agents}") agentsFolder: File,
    ): ScriptingLLMFunctionLoader {
        return ScriptingLLMFunctionLoader(beanProvider, functionScriptEngine).also { loader ->
            if (agentsFolder.exists()) agentsFolder.listFiles()?.let { loader.loadFunctions(*it) }
        }
    }

    @Bean
    fun agentLoader(agentFactory: AgentFactory<*>) = Agents(agentFactory)

    @Bean
    fun functionLoader(beanProvider: BeanProvider) = Functions(beanProvider)
}
