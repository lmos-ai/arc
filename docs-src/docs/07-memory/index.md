---
title: Memory
---

This page lists Memory  


## In-Memory Memory

The Arc Agent Framework provides a default in-memory implementation of the `Memory` interface.
The implementation is automatically configured when the Arc Spring Boot Starter is used 
and no other implementation of the `Memory` interface is provided.

This implementation is good for getting started, but it is not recommended for production use.



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
