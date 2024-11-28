---
title: Spring Boot Integration
sidebar_position: 2
---

The Arc Framework can easily be integrated into any Spring Boot application.

For this purpose, the `arc-spring-boot-starter` library is provided.

>
> Also check out Spring Boot Init project here: https://github.com/lmos-ai/arc-spring-init 
> 

### Getting Started

Add the following dependencies to your project to get started.
For example, in the `build.gradle.kts` file.

```kts
implementation("ai.ancf.lmos:arc-spring-boot-starter:$arcVersion")

// recommended, but not required
implementation("org.springframework.boot:spring-boot-starter-actuator")
implementation("io.micrometer:micrometer-registry-prometheus")
```

The `arc-spring-boot-starter` library will set up the necessary beans and configurations
to run the Arc Agents within the Spring Boot application.

To better understand what Arc Agents Components that loaded see [Manual Setup](/docs/manual_setup).


### Agent Scripting

To load Arc Agents defined in script files, add the `arc-scripting` dependency to your project.

```kts
implementation("ai.ancf.lmos:arc-scripting:$arcVersion")
```

The following properties can be set in the `application.yml` file
to configure the Arc Agent Scripting.

```yaml
arc:
  scripts:
    folder: ./agents // the folder to load the scripts from
    hotReload:
      enable: true
      delay: PT1S 
```

Now any Agent and Agent Functions defined in the `./agents` folder will be loaded and run by the Arc Application.


### Agents Beans

Agents can also be defined as Spring Beans, see the [Spring Beans](/docs/spring/agent-beans) documentation for more information.


### Adding the Azure OpenAI Client

To automatically add an `AzureAIClient` instance in your Spring Container, 
simply add the `arc-azure-client` dependency to your project.

```kts
    implementation("ai.ancf.lmos:arc-azure-client:$arcVersion")
```

You may also want to add the Azure Identity library if you require a different authentication than an API-Key.
See https://learn.microsoft.com/en-us/azure/developer/java/sdk/authentication/overview

```kts
implementation("com.azure:azure-identity:1.13.1")
```

Then configure the client in the `application.yml` file:

```yaml
arc:
  ai:
    clients:
      # To connect to an OpenAI Model
      - id: GPT-4o
        model-name: gpt-4o
        api-key: ${OPENAI_API_KEY}
        client: openai
        
      # To connect to an Azure OpenAI Model (requires the Azure Identity library and "az login" to be setup)
      - id: GPT-4o-Azure
        model-name: gpt-4o
        url: ${AZURE_OPENAI_URL}
        client: azure      
        
      # To connect to an Azure OpenAI Model with an API Key
      - id: GPT-4o-Azure-ApiKey
        model-name: gpt-4o
        url: ${AZURE_OPENAI_URL}
        api-key: ${AZURE_API_KEY}
        client: azure
```


### Adding LangChain4J Clients

The Arc Framework can also use LangChain4J clients to connect to different language models.
Simply add the `langchain4j` libraries that are required for your project.

Check [LangChain4J](/docs/clients/langchain4j) for the list of available clients.

For example:
```kts
  implementation("dev.langchain4j:langchain4j-bedrock:$langchain4jVersion")
  implementation("dev.langchain4j:langchain4j-google-ai-gemini:$langchain4jVersion") 
  implementation("dev.langchain4j:langchain4j-ollama:$langchain4jVersion")
```

Then configure the client in the `application.yml` file. For example:

```yaml
arc:
  ai:
    clients:
      # Ollama (models running locally)
      - id: llama3:8b
        modelName: llama3:8b
        client: ollama      
      
      # Models hosted on Amazon Bedrock
      - id: anthropic
        url: eu-central-1
        accessKey: $ACCESS_KEY
        accessSecret: $ACCESS_SECRET
        modelName: anthropic.claude-3-5-sonnet-20240620-v1:0
        client: bedrock 
        
      # Gemini 
      - id: gemini
        modelName: gemini-1.5-flash
        url: $GEMINI_URL
        apiKey: $GEMINI_API_KEY
        client: gemini
```

### Interacting with Agents

Once the Arc Agents are running, you can interact with them through the `AgentProvider` bean.

```kotlin
@Bean
val agentProvider: AgentProvider

agentProvider.getAgents()
```

Alternatively, you can use the Agent GraphQL API. See the [GraphQL API](/docs/spring/graphql) documentation for more information.
