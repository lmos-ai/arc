---
title: Memory
---

The Arc Agent Framework declares the following interface for memory:

```kotlin
interface Memory {
    
    /**
     * Store a value in LONG_TERM memory.
     * @param owner The owner of the memory. For example, the user id.
     * @param key The key to store the value under.
     * @param value The value to store. If null, the value is removed from memory.
     */
    suspend fun storeLongTerm(owner: String, key: String, value: Any?)

    /**
     * Store a value in SHORT_TERM memory.
     * @param owner The owner of the memory. For example, the user id.
     * @param key The key to store the value under.
     * @param value The value to store. If null, the value is removed from memory.
     * @param sessionId The session id to store the value under.
     */
    suspend fun storeShortTerm(owner: String, key: String, value: Any?, sessionId: String)

    /**
     * Fetch a value from memory.
     * @param owner The owner of the memory. For example, the user id.
     * @param key The key to fetch the value for.
     * @param sessionId The session id to fetch the value for. Only used if the value was stored under SHORT_TERM memory.
     * @return The value stored under the key, or null if no value is stored.
     */
    suspend fun fetch(owner: String, key: String, sessionId: String? = null): Any?
}
```

Each application wanting to use memory can provide an implementation of the `Memory` interface.

Arc provides the following implementations:

## In-Memory Memory

The Arc Agent Framework provides a default in-memory implementation of the `Memory` interface.
The implementation is automatically configured when the Arc Spring Boot Starter is used 
and no other implementation of the `Memory` interface is provided.

This implementation is good for getting started, but it is not recommended for production use, 
as memory is not persisted or shared between instances.


## Mongo Memory

| Package Name                         | Type                |
|--------------------------------------|---------------------|
| io.github.lmos-ai.arc:arc-memory-mongo-spring-boot-starter:$arcVersion | Spring Boot Starter |

The Mongo Memory implementation uses the [Mongo Database](https://www.mongodb.com/) to store data.

The module is provided as a Spring Boot Starter and under the hood uses Spring Data to access
the Mongo Database.

A time-to-live (TTL) index is created for `short-term` memory entries to automatically remove
them after a period of time.

| Configuration         | Description                                                                                                       | Type     | Default        |
|------------------------------------------|-------------------------------------------------------------------------------------------------------------------|----------|----------------|
| arc.memory.short-term-ttl | The time-to-live for short-term memory entries.                                                                   | Duration | PT3H (3 hours) |
| spring.data.mongodb.uri | The uri of the Mongo Database. Example, "mongodb://admin:password@localhost:27017/memory" | URI      | localhost      |

For more details on how to configure a Mongo Database with Spring, please refer to 
https://docs.spring.io/spring-data/mongodb/reference/mongodb.html.
