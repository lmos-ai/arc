---
title: Manual Setup
sidebar_position: 2
---

The Arc Framework can easily be setup with a few lines of code to run in any JVM application.

> Note: When using Spring Boot, it is recommended to use the [Arc Spring Boot Starter](/docs/spring) 
> so that all the following steps are done automatically.

> Also: read the [Component Overview](/docs/component_overview) page for a better understanding of core components of the Framework.

The best way to understand how to set up the Arc Framework is to look at the following example:

- https://github.com/lmos-ai/arc/tree/main/arc-runner/src/main/kotlin/server/ArcSetup.kt


### Loading Scripted Agents

Arc Agent DSL scripts can also be loaded from a file, folder, or string.

```kotlin
val agentLoader: ScriptingAgentLoader

agentLoader.loadAgents(File("my-agent.agent.kts"))

agentLoader.loadAgentsFromFolder("agents")

agentLoader.loadAgent("""
  agent {
     name = "simple-agent"
     model = { "modelId" }
     prompt {
      "You are a helpful agent." 
     }
  }
""") 

```


#### Hot Reloading Scripts

Arc Agent DSL script files can be hot-loaded when modified.

```kotlin

val scriptHotReload = ScriptHotReload(agentLoader, functionLoader, 3.seconds)
scriptHotReload.start(File("./agents"))

```

> Note: In order for Agents Scripts to be correctly identified, their files must end with `.agent.kts` when containing Agents and
> `.functions.kts` when containing Functions. This will enable an IDE, such as the IntelliJ IDE,
> to provide syntax highlighting and code completion.

Once loaded, Scripted Agents are no different from Agents loaded by other
mechanisms.

### Executing Agents
Once an Agent is loaded, it can be executed by passing a `Conversation` object to the `execute` method.

```kotlin
 val agent = agentLoader.getAgentByName(agentName) as ChatAgent? ?: error("Agent not found!")
 val conversation = Conversation(User("anonymous")) + UserMessage("My question")
 val result = agent.execute(conversation).getOrNull()
```


### Defining Agents (without scripting)

Loading Agent from scripting files is a great way to develop and prototype Agents.
However, the Agent DSL can also be used to create Agents programmatically.

Example:

```kotlin
import ai.ancf.lmos.arc.agents.dsl.buildAgents
import ai.ancf.lmos.arc.agents.dsl.buildFunctions

   val loadedAgents = buildAgents(agentFactory) {
        agent {
            name = "MyAgent"
            description = "My agent"
            tools {
                +"get_content"
            }
            prompt {
                """
                 Always answer with 'Hello, World!'. 
                """
            }
        }
    }

    val functions = buildFunctions(beanProvider) {
        function(
            name = "get_content",
            description = "Returns content from the web.",
            params = types(
                string("url", "The URL of the content to fetch.")
            )
        ) { (url) ->
            httpGet(url.toString())
        }
    }

```
