---
title: Defining Agents
sidebar_position: 1
---

Agents are defined using the Arc Agent DSL.
The following fields are mandatory when defining an agent.

- **name**: the name of the Agent. There should be a unique identifier. Preferably without special characters.
- **model (optional)**: the model that should be provided to the Agent.
- **description**: a short description of what the Agent does.
- **prompt**: the System Prompt of the Agent.
  The `prompt` defines the objective, goals and instructions for Agents.
  They are built dynamically an every request.
- **tools**: a list of tools/functions that the Agent uses. Tools are referenced by their group name.
- **filterInput**: definition of filter logic. 

```kts
agent {
    name = "weather"
    model = { "gemma:7b" }
    description = "Agent that provides weather data."
    prompt { """
       You are a professional weather service.
       You have access to real-time weather data with the get_weather function.
       Keep your answer short and concise.
       All you require is the location.
       if you cannot help the user, simply reply "I cant help you"
     """
    }
    tools = listOf("get_weather")
}
```

See the following pages on how to load the agents into your application.
- [Manual Setup](../manual_setup)
- [Spring Boot Beans](../spring/agent-beans)


### Prompt templating

The `prompt` function of an Agent is called on each request. 
Meaning that it can be dynamically customized to best suit the current context. 
Now although Kotlin Strings are quite powerful, 
adding logical constructs such as `if` and `for loops` statements can be cumbersome.

For this purpose, the Arc DSL overwrites the UnaryPlus operator, `+`, within the `prompt` block
to allow for simple string concatenation.

For example, the following code snippet shows how to use the `+` operator to build a dynamic prompt.
```kts
agent {
    prompt {
        +"Here is the first part og the prompt."
        if(someCondition) {
            +"Here is a conditional part of the prompt."
        }
      "The last part of the prompt (this does not require a + because it is automatically returned)."
    }
}
```

