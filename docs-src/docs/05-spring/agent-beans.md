---
title: Agent Beans
---

The Arc Agent Framework has first-class support for Spring Boot applications.
Therefore, the Arc Agents DSL can also be used to define Spring Boot beans. 
This may be a better approach than using Kotlin Scripting if there are any security 
concerns for production applications or if the application is to be complied to a native image using GraalVM.

The Arc Agent DSL can be directly used in Spring Boot `@Configuration` and `@SpringBootApplication` classes.

:::info 
The name of the bean and the name of the Agent may not be the same.
:::

For example,
```kotlin

import io.github.lmos.arc.spring.Agents
import io.github.lmos.arc.spring.Functions

@Configuration
class Configuration {
    
   @Bean
   fun myAgent(agent: Agents) = agent {
     name = "My Agent"
     prompt { "you are a helpful weather agent." }
     tools = listOf("get_weather")
   }

  @Bean
  fun myFunction(function: Functions) = function(
    name = "get_weather",
    description = "Returns real-time weather information for any location",
  ) {
    """
        The weather is good in Berlin. It is 20 degrees celsius.
    """
  }
  
}
```

Once declared as beans, the Agents can either be injected into other components or 
retrieved from the `AgentProvider` (provided the `arc-spring-boot-starter` is used).

```kotlin
@Bean
val agentProvider: AgentProvider

agentProvider.getAgentByName("My Agent")
```