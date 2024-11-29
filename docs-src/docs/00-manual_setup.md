---
title: Manual Setup
sidebar_position: 2
---

The Arc Framework can easily be setup with a few lines of code.

> Note: When using Spring Boot, it is recommended to use the [Arc Spring Boot Starter](/docs/spring) 
> so that all the following steps are done automatically.

### Loading Agents
The following shows how to load Scripted Arc Agents.

(See [Defining Agents (without scripting)](#defining-agents-without-scripting) 
for an example of defining Agents programmatically.)

```kotlin
val chatCompleterProvider = ChatCompleterProvider { modelId ->
    // Return a ChatCompleter/AIClient for the given model id.
}

val beanProvider = SetBeanProvider(setOf(chatCompleterProvider))
val functionLoader = ScriptingLLMFunctionLoader(beanProvider, KtsFunctionScriptEngine())
val agentFactory = ChatAgentFactory(CompositeBeanProvider(setOf(functionLoader), beanProvider))
val agentLoader = ScriptingAgentLoader(agentFactory, KtsAgentScriptEngine())

agentLoader.loadAgent("""
  agent {
     name = "simple-agent"
     model = { "modelId" }
     prompt {
      "You are a helpful agent." 
     }
  }
""") 

val loadedAgents = agentLoader.getAgents()
```

#### ChatCompleterProvider
The `ChatCompleterProvider` is a function that returns a `ChatCompleter` for a given model id.
The model id comes from the `model` field of an agent.

A `ChatCompleter` is the interface implemented by LLM clients. See the [Clients](/docs/clients) section for more details.

Also checkout the [LangChain4J](/docs/clients/langchain4j) client for an example of a `ChatCompleterProvider`.

#### SetBeanProvider
The `SetBeanProvider` implements the `BeanProvider` interface.
A `BeanProvider` provides the beans that are used within the Arc Agents.
These beans can be accessed from anywhere within the Agent DSL using the `get<BeanClass>()` method.

At least a `ChatCompleterProvider` must be provided to the `BeanProvider`.
It is required by the `ChatAgent` to complete the conversation.

Also an instance of `LLMFunctionLoader`, in this example `ScriptingLLMFunctionLoader`, 
should also be provided so that the Agents have access to the functions defined in the DSL.

#### ScriptingLLMFunctionLoader
The `ScriptingLLMFunctionLoader` is an instance of `LLMFunctionLoader`.
`LLMFunctionLoader`s are responsible for loading Agent functions. 

The `ScriptingLLMFunctionLoader` loads functions from kotlin script files.

#### ChatAgentFactory
The `ChatAgentFactory` is responsible for creating `ChatAgent` instances from the agent DSL. 

The ChatAgent is the main implementation of the Arc Agent interface.

As the name suggests, `ChatAgent`s are agents that conduct a conversation with the client/user.
They take a `Conversation` object as input and
outputs the `Conversation` containing a new message.
See the [Conversations](#conversations) section for more details on conversations.

#### ScriptingAgentLoader
The `ScriptingAgentLoader` is an instance of `AgentLoader`.
`AgentLoader`s are responsible for loading Agents. 

The `ScriptingAgentLoader` loads agents from kotlin script files.

#### Hot Reloading Scripts
A powerful and flexible way of crafting Arc Agents is to use Kotlin Scripting.
In this case, the Arc Agent DSL is placed in Kotlin script files that can be loaded and executed dynamically
at runtime without restarting the application, i.e. "Hot Reloaded".

Scripts can be loaded from any source and passed to the `loadAgent` method as a string.
Alternatively, Agents can be loaded from a folder and reload automatically when the files are modified.

```kotlin
  
val scriptHotReload = ScriptHotReload(
    ScriptingAgentLoader(agentFactory, agentScriptEngine),
    ScriptingLLMFunctionLoader(beanProvider, functionScriptEngine),
    3.seconds, // fallback polling interval if file watcher is not supported on the platform
)
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

See the [cookbook](/docs/cookbook/) for examples of Agent Scripts.


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


### Conversations

The `Conversation` object represents a conversation between a client and an Arc Agent, such as a ChatAgent.
It contains the entire transcript of the conversation, both client and agent messages,
plus some metadata about the conversation.

Each `Conversation` object must be associated with a `User` which acts as the owner of the conversation.
This to prevent a conversation from being accessed by unauthorized users/clients.
Furthermore, the `User` id may also be used as a key to store user/client specific data.

Messages are usually be one of 2 types:

- `UserMessage` - messages sent by the user/client.
- `AssistantMessage` - messages generated by the Agent.

`SystemMessage`s, messages that contain instructions for the Agent, are usually not store in the conversation
transcript.
