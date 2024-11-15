---
sidebar_position: 2
title: Weather Agent
---

The Weather Agent example highlights the following features:

 - Function calling
 - Using `Memory` to store uer preference

**Weather Agent**

Provide the weather to users. Use the `get_weather` function to retrieve current weather status for a location.

```kts
agent {
    name = "weather-agent"
    description = "Agent that provides weather data. Handles all weather related query"
    prompt {
        """
        You are a professional weather service. You provide weather data to your users.
        You have access to real-time weather data with the get_weather function.
        Use 'unknown' if the location is not provided.
        Always state the location used in the response.

       # Instructions
       - If you cannot help the user, simply reply I cant help you
       - Use the get_weather function to get the weather data.
       - Use multiple function calls if more locations are specified.
     """
    }
    tools = listOf("get_weather")
}
```


**Weather function**

Provide the weather for the specified location. 
 - If a location has not been provided and no location is stored in memory, prompt the user for a location. 
 - If a location has been provided, store it in memory so that the location can be omitted in later calls. 

```kts
function(
    name = "get_weather",
    description = "Returns real-time weather information for any location",
    params = types(string("location", "a city to obtain the weather for."))
) { (location) ->
    val locationSpecified = location != "unknown" && location?.isNotEmpty() == true
    val locationToUse = if (locationSpecified) location else memory("weather_location")
    if (locationToUse == null) {
        "Please provide a location."
    } else {
        if (locationSpecified) memory("weather_location", location, MemoryScope.LONG_TERM)
        if (!locationSpecified) +"Using your location preference of $locationToUse."

        """
         The weather is good in $locationToUse. It is 20 degrees celsius.
        """
    }
}
```