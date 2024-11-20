// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.spring

import ai.ancf.lmos.arc.agents.AgentLoader
import ai.ancf.lmos.arc.agents.dsl.BasicAgentDefinitionContext
import ai.ancf.lmos.arc.agents.dsl.BasicFunctionDefinitionContext
import ai.ancf.lmos.arc.agents.dsl.BeanProvider
import ai.ancf.lmos.arc.agents.dsl.ChatAgentFactory
import ai.ancf.lmos.arc.agents.functions.LLMFunctionLoader
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean

@ConditionalOnProperty("arc.scripts.enabled", havingValue = "false")
class CompiledScriptsConfiguration {

    @Bean
    @ConditionalOnClass(ai.ancf.lmos.arc.agents.gen.Agents::class)
    fun agents(agentFactory: ChatAgentFactory) = AgentLoader {
        val context = BasicAgentDefinitionContext(agentFactory)
        with(context) {
            ai.ancf.lmos.arc.agents.gen.Agents().build()
        }
        context.agents.toList()
    }

    @Bean
    @ConditionalOnClass(ai.ancf.lmos.arc.agents.gen.Functions::class)
    fun functions(beanProvider: BeanProvider) = LLMFunctionLoader {
        val context = BasicFunctionDefinitionContext(beanProvider)
        with(context) {
            ai.ancf.lmos.arc.agents.gen.Functions().build()
        }
        context.functions.toList()
    }
}
