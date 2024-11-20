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
import org.springframework.beans.factory.annotation.Value
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import kotlin.reflect.KClass

@AutoConfiguration
@Import(
    MetricConfiguration::class,
    ClientsConfiguration::class,
    Langchain4jConfiguration::class,
    ScriptingConfiguration::class,
    CompiledScriptsConfiguration::class,
)
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
    @ConditionalOnMissingBean(AgentProvider::class)
    fun agentProvider(loaders: List<AgentLoader>, agents: List<Agent<*, *>>): AgentProvider =
        CompositeAgentProvider(loaders, agents)

    @Bean
    fun llmFunctionProvider(loaders: List<LLMFunctionLoader>, functions: List<LLMFunction>): LLMFunctionProvider =
        CompositeLLMFunctionProvider(loaders, functions)

    @Bean
    fun agentLoader(agentFactory: AgentFactory<*>) = Agents(agentFactory)

    @Bean
    fun functionLoader(beanProvider: BeanProvider) = Functions(beanProvider)
}
