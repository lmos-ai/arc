---
title: Eventing
---

Evening is at the core of the Arc Agents framework.
The Arc Agent Framework provides the following interfaces for enabling eventing
between your application and components of the Arc Agent Framework:

```kotlin
  /**
 * EventPublisher interface.
 */
fun interface EventPublisher {
    fun publish(event: Event)
}

/**
 * Add EventHandlers to the implementation of this event to receive events.
 */
fun interface EventListeners {
    fun add(handler: EventHandler<out Event>)
}
```

Out of the box, components of the Arc Framework will publish various events.
The events are useful for monitoring the system.

Custom events can be easily published throughout the Arc Agent DSL using the
`emit` function.

```kotlin
  agent {
    name = "..."
    description = "..."
    prompt { "..." }
    filterInput {
        emit(MyCustomEvent())
    }
}
```

Currently, the following events are published by the framework:

### Agent Events

| Event              |                                             |  
|--------------------|---------------------------------------------|
| AgentStartedEvent  | Published whenever an Agent processes data. |
| AgentFinishedEvent | Published after an Agent processes data.    |   


### AI Client Events

| Event            |                                                 |  
|------------------|-------------------------------------------------|
| LLMStartedEvent  | Published whenever an AI Client processes data. |
| LLMFinishedEvent | Published after an AI Client processes data.    |   
| LLMFunctionCalledEvent | Published after an LLM function was called.     |   
