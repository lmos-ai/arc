---
title: LangChain4j
---

LangChain4j is s Java library that provides a uniform interface to multiple language models.

See https://docs.langchain4j.dev/

The Arc Framework provides a wrapper for the LangChain4j ChatLanguageModel interface, 
which allows us to use any LangChain4j client within our Arc Agents.

Supported LangChain4j clients:

| Model          | Package                                      | Supported Version |  
|----------------|----------------------------------------------|-------------------|
| Amazon Bedrock | dev.langchain4j:langchain4j-bedrock          | 0.36.2            | 
| Google Gemini  | dev.langchain4j:langchain4j-google-ai-gemini | 0.36.2            |   
| Ollama.        | dev.langchain4j:langchain4j-ollama           | 0.36.2            |   
| Groq           | dev.langchain4j:langchain4j-open-ai          | 0.36.2            |   


Check out the following example on how to set up clients:

- https://github.com/lmos-ai/arc/tree/main/arc-runner/src/main/kotlin/server/ChatCompleterProvider.kt
