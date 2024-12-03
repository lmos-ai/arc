---
title: Packages
---

## Basic Packages
```kts
val arcVersion = "0.115.0"
implementation("ai.ancf.lmos:arc-agents:$arcVersion")
```

## DSL Scripting
```kts
implementation("ai.ancf.lmos:arc-scripting:$arcVersion")
```

## AI Clients
```kts
// Add the Azure OpenAI client library for Java
implementation("ai.ancf.lmos:arc-azure-client:$arcVersion")

// Add the langchain4j dependencies for the AIClient that should be used.
val langchain4jVersion = "0.36.2"
implementation("dev.langchain4j:langchain4j-bedrock:$langchain4jVersion")
implementation("dev.langchain4j:langchain4j-google-ai-gemini:$langchain4jVersion")
implementation("dev.langchain4j:langchain4j-ollama:$langchain4jVersion")
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