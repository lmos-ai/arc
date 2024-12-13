---
title: Manual Setup
sidebar_position: 2
---

Usually the Arc Agent Framework will be used with the Spring Boot Starter
or the Arc Runner, which will automatically set up the framework for you.

However, if you want to set up the framework manually and use it in a different frameworks or environments, 
you can do so by following the steps below.

> Also: read the [Component Overview](/docs/component_overview) page for a better understanding of core components of the Framework.


### Loading Agents

The `DSLAgents` is a convenient way to load Agents that are defined with Kotlin DSL.

```kotlin
 val chatCompleterProvider = { AzureAIClient(...) } // or any other AIClient

 val agentBuilder = DSLAgents.init(chatCompleterProvider).apply {
    define {
        agent {
            name = "agent"
            description = "agent description"
            prompt { "Agent prompt goes here." }
        }
    }

    defineFunctions {
        function(
            name = "get_weather",
            description = "the weather service",
            params = types(string("location", "the location")),
        ) {
            httpGet("https://api.weather.com/$location")
        }
    }
}

val agents = agentBuilder.getAgents()

```


### Loading Scripted Agents

The `DSLScriptAgents` class can be used to load Agents that are defined with kotlin scripts.

```kotlin
val chatCompleterProvider = { AzureAIClient(...) } // or any other AIClient

 val agentBuilder = DSLScriptAgents.init(chatCompleterProvider).apply {
    define(
        """
            agent {
                name = "agent"
                description = "agent description"
                prompt { "Agent prompt goes here." }
            }
        """,
    ).getOrThrow()

    defineFunctions(
        """
            function(
                name = "get_weather",
                description = "the weather service",
                params = types(string("location", "the location")),
            ) { location ->
               httpGet("https://api.weather.com/${"$"}location")
            }
        """,
    )
}

val agents = agentBuilder.getAgents()

```


### Executing Agents
Once an Agent is loaded, it can be executed by passing a `Conversation` object to the `execute` method.

```kotlin
 val agent = agentBuilder.getAgentByName(agentName) as ChatAgent? ?: error("Agent not found!")
 val conversation = Conversation(User("userOrClientId")) + UserMessage("My question")
 val result = agent.execute(conversation).getOrNull()
```

