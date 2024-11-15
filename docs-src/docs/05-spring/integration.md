---
title: Spring Boot Integration
sidebar_position: 2
---

The Arc libraries are commonly used in Spring Boot applications.
For this purpose, the `arc-spring-boot-starter` library is provided.
Add the following dependencies to your project.
For example, in the `build.gradle.kts` file.

```kts
implementation("io.github.lmos-ai.arc:arc-scripting:$arcVersion")
implementation("io.github.lmos-ai.arc:arc-azure-client:$arcVersion")
implementation("io.github.lmos-ai.arc:arc-spring-boot-starter:$arcVersion")
```

The `arc-spring-boot-starter` library will set up the necessary beans and configurations
to run the Arc Agents within a Spring Boot application. Providing many defaults that can be overridden.

The only bean that is required to be defined is the `ChatCompleterProvider` bean.
The `ChatCompleterProvider` bean provides instances of the `ChatCompleter` interface.

The `ChatCompleter` interface is usually backed by an AI service that can complete text,
such as Large Language Models.
The `ChatCompleterProvider` enables these instances to be created dynamically
based on the current context.

One implementation of the `ChatCompleter` interface is the `AzureAIClient` class.
This class uses the Azure OpenAI API.

Example of Spring Boot configuration
```kotlin
    @Bean
    fun aiClient(languageModel: LanguageModel, client: OpenAIAsyncClient, eventPublisher: EventPublisher) =
        AzureAIClient(languageModel, client, eventPublisher)

    @Bean
    fun chatCompleterProvider(client: AzureAIClient) = ChatCompleterProvider { client }
```

### Agent Scripting

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
