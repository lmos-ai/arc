---
title: Defining Functions
---

Functions are an important building block of Agents. They are used to access external services and data.
An Agent can be assigned multiple functions. 
Agents choose which functions to use based on the function description.

Under the hood, these functions translate to LLM Functions, for example,
OpenAI functions (https://platform.openai.com/docs/guides/function-calling).

:::info 
Not all Models/Clients support functions.
:::

Here is an example of a Weather Agent assigned a `get_weather` function and `get_time` function.

```kts
agent {
    name = "weather"
    description = "Agent that provides weather data."
    prompt { """ Some system prompt """ }
    tools {
        +"get_weather"
        +"get_time"
    }
}
```

Example `weather.functions.kts`

```kts
function(
  name = "get_weather",
  description = "Returns real-time weather information for any location",
  params = types(string("location", "a city to obtain the weather for."))
) { (location) ->
    """
        The weather is good in $locationToUse. It is 20 degrees celsius.
    """
  }

```

Functions always return a string. This string can contain natural language text, JSON, or any other format
or a combination of them.

### Assigning Functions conditionally

Functions can be assigned conditionally based on the Agent's state or the user's input.
Within the `tools` block, you can use the `get` function 
to access the current request or any other bean contained within the context of the Agent.

Here is an example:
```kts
agent {
    name = "weather"
    description = "Agent that provides weather data."
    prompt { """ Some system prompt """ }
    tools {
        val isBeta = get<SomeCustomBean>().isBeta()
        if(isBeta) +"get_weather_forecast"
        +"get_weather"
        +"get_time"
    }
}
```
(`SomeCustomBean` is a custom bean, not provided by the framework)

### Automatic assign all available functions

If you want to assign all available functions to an Agent, you can use the `AllTools` constant.

Example:
```kts
agent {
    name = "weather"
    description = "Agent that provides weather data."
    prompt { """ Some system prompt """ }
    tools = AllTools
}
```

This will supply all functions that are available in the application to the Agent.