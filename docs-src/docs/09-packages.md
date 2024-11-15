---
title: Packages
---

## Basic Packages
```kts
val arcVersion = "0.102.0"
implementation("ai.ancf.lmos:arc-scripting:$arcVersion")
```

## AI Clients
```kts
implementation("ai.ancf.lmos:arc-azure-client:$arcVersion")
implementation("ai.ancf.lmos:arc-ollama-client:$arcVersion")
implementation("ai.ancf.lmos:arc-gemini-client:$arcVersion")
```

## Spring Boot Packages
```kts
implementation("ai.ancf.lmos:arc-spring-boot-starter:$arcVersion")
implementation("ai.ancf.lmos:arc-memory-mongo-spring-boot-starter:$arcVersion")
```

## GraphQL
```kts
implementation("ai.ancf.lmos:arc-api:$arcVersion")
implementation("ai.ancf.lmos:arc-graphql-spring-boot-starter:$arcVersion")
```

## Extensions 
```kts
implementation("ai.ancf.lmos:arc-reader-pdf:$arcVersion")
implementation("ai.ancf.lmos:arc-reader-html:$arcVersion")
```