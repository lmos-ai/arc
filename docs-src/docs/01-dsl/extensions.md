---
title: Extensions
---

Extensions are functions that can be called throughout 
the Arc Agent DSL and provide additional functionality, such as reading data, logging, and more.

`Redaers` are also functions. They are described in the [Readers](../readers) section.

## System and User Context

Providing the LLM context is a very important part of Prompt Engineering.
The functions `system` and `userProfile` can be used to retrieve helpful information, 
such as the user's name or the default language.

**So where do these values come from?**

Simply implement the corresponding interfaces, `SystemContextProvider` and/or `UserProfileProvider` 
and provide them as beans to your Arc Agents.

If running Arc in Spring Boot, then these can be implemented as beans in your Spring Boot configuration.

If using the GraphQL package, then these values are taken from the GraphQL request, 
see [GraphQL](../spring/graphql).

```kotlin
interface SystemContextProvider {
    fun provideSystem(): SystemContext
}

interface UserProfileProvider {
    fun provideProfile(): UserProfile
}

// Implement the interfaces and pass them to the agent
agent.execute(conversation, setOf(systemContextProvider, userProfileProvider))
      
```

Example:
```kotlin
agent {
    name = "weather"
    description = "Agent that provides weather data."
    prompt { 
        val customerName = userProfile("name", "")
        val lang = system("defaultLanguage", "en")
        """ Some system prompt """ 
    }
}
```


## Logging
The logging functions, `debug`, `info`, `warn`, and `error`, 
can be called within the Agent DSL to log messages.

Example using the warn function in a filter block.
```kotlin
agent {
    name = "weather"
    description = "Agent that provides weather data."
    prompt { """ Some system prompt """ }
    filterInput {
        if(inputMessage.content.contains("Waldo")) warn("Found Waldo")
    }
}
```

The functions use a the logger with the name `ArcDSL`.

The name of the logger is required, for example, when using Spring Boot.
For example, to configure the log level in the `application.yml`:
```yaml
level:
    root: INFO
    ArcDSL: DEBUG
```


## LLM

The `llm` function calls an LLM model to generate a response. 
Unless specified otherwise, the default LLM model is used.

Example using the warn function in a filter block.
```kotlin
agent {
    name = "weather"
    description = "Agent that provides weather data."
    prompt { """ Some system prompt """ }
    filterOutput {
        outputMessage = llm("Generate a different response")
    }
}
```



## Time, Date, Year

The `time`,`date` and `year` functions can be used to provide the current date to the LLM.

Example:
```kotlin
agent {
    name = "weather"
    description = "Agent that provides weather data."
    prompt { """
     The current time is ${time()}. // returns 12:00
     The current date is ${date()}. // returns 28.09
     The current year is ${year()}. // returns 2024
     """ }
}
```

To ensure that the correct time zone is used, the `zoneId` parameter can be set.

```kotlin   
 time(zoneId = "Europe/Berlin")
``` 