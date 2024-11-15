---
title: Extensions
---

Extensions are functions that can be called throughout 
the Arc Agent DSL and provide additional functionality, such as reading data, logging, and more.

`Redaers` are also functions. They are described in the [Readers](../readers) section.

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