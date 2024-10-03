// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.spring

import ai.ancf.lmos.arc.agents.Agent
import ai.ancf.lmos.arc.agents.AgentLoader
import ai.ancf.lmos.arc.agents.AgentProvider
import ai.ancf.lmos.arc.agents.CompositeAgentProvider
import ai.ancf.lmos.arc.agents.dsl.AgentFactory
import ai.ancf.lmos.arc.agents.dsl.BeanProvider
import ai.ancf.lmos.arc.agents.dsl.ChatAgentFactory
import ai.ancf.lmos.arc.agents.dsl.CoroutineBeanProvider
import ai.ancf.lmos.arc.agents.events.*
import ai.ancf.lmos.arc.agents.functions.CompositeLLMFunctionProvider
import ai.ancf.lmos.arc.agents.functions.LLMFunction
import ai.ancf.lmos.arc.agents.functions.LLMFunctionLoader
import ai.ancf.lmos.arc.agents.functions.LLMFunctionProvider
import ai.ancf.lmos.arc.agents.llm.TextEmbedderProvider
import ai.ancf.lmos.arc.agents.memory.InMemoryMemory
import ai.ancf.lmos.arc.agents.memory.Memory
import ai.ancf.lmos.arc.agents.router.SemanticRouter
import ai.ancf.lmos.arc.agents.router.SemanticRoutes
import ai.ancf.lmos.arc.scripting.ScriptHotReload
import ai.ancf.lmos.arc.scripting.agents.AgentScriptEngine
import ai.ancf.lmos.arc.scripting.agents.CompiledAgentLoader
import ai.ancf.lmos.arc.scripting.agents.KtsAgentScriptEngine
import ai.ancf.lmos.arc.scripting.agents.ScriptingAgentLoader
import ai.ancf.lmos.arc.scripting.functions.FunctionScriptEngine
import ai.ancf.lmos.arc.scripting.functions.KtsFunctionScriptEngine
import ai.ancf.lmos.arc.scripting.functions.ScriptingLLMFunctionLoader
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
@Import(MetricConfiguration::class, ClientsConfiguration::class)
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
    @ConditionalOnProperty("arc.router.enable", havingValue = "true")
    fun semanticRouter(
        @Value("\${arc.router.model}") model: String,
        textEmbedderProvider: TextEmbedderProvider,
        agentProvider: AgentProvider,
        initialRoutes: SemanticRoutes? = null,
        eventPublisher: EventPublisher,
    ): SemanticRouter {
        return SemanticRouter(textEmbedderProvider.provideByModel(model), initialRoutes, eventPublisher)
    }

    @Bean
    @ConditionalOnProperty("arc.scripts.hotReload.enable", havingValue = "true")
    fun scriptHotReload(
        @Value("\${arc.scripts.folder:/agents}") agentsFolders: List<File>,
        @Value("\${arc.scripts.hotReload.delay:PT3M}") hotReloadDelay: Duration,
        agentLoader: ScriptingAgentLoader,
        functionLoader: ScriptingLLMFunctionLoader,
    ): ScriptHotReload {
        agentsFolders.forEach { if (!it.exists()) error("Agents folder does not exist: $it!") }
        return ScriptHotReload(agentLoader, functionLoader, hotReloadDelay.toKotlinDuration()).also { r ->
            agentsFolders.forEach { folder -> folder.walk().filter { it.isDirectory }.forEach { r.start(it) } }
        }
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
            if (agentsFolder.exists()) agentsFolder.walk().filter { it.isFile }.forEach { loader.loadAgents(it) }
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
            if (agentsFolder.exists()) agentsFolder.walk().filter { it.isFile }.forEach { loader.loadFunctions(it) }
        }
    }

    @Bean
    fun agentLoader(agentFactory: AgentFactory<*>) = Agents(agentFactory)

    @Bean
    fun functionLoader(beanProvider: BeanProvider) = Functions(beanProvider)
}
