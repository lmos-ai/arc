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
    tools = listOf("get_weather", "get_time")
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