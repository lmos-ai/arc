---
slug: Spring.AI-integration
title: Sptring.ai integration using the SpringChatClient
authors:  
- name: Max
tags: ["spring", "Spring.AI", "feature"]
---

As the world of AI continues to evolve and develop,
the need of integration of AI services into your applications 
has become increasingly valuable.

Since ARC's goal is to make the process of integration, management 
and creation of AI in your existing services as seamless as possible 
it only made sense to integrate Spring.AI to work in a plug and play 
fashion.
 

In this blog post, we'll explore how to implement a new adapter for 
Spring.AI to work within ARC.
To expand its capabilities and make it easier to incorporate various 
AI services into your projects.

**What is an the `SpringChatClient` Adapter in ARC?**

In ARC, the `SpringChatClient` acts like a bridge between the framework and 
external Spring.AI models/ APIs. It enables you to seamlessly integrate any 
Spring.AI ChatModels in your ARC application, allowing for easy access to their 
functionality. These Adapters/ ChatClients can be used to connect to various 
AI platforms, such as Google VertexAI, Amazon Comprehend, Grog, Mistral.Ai, 
IBM Watson or many [more](https://docs.spring.io/spring-ai/reference/api/chatmodel.html).

**Why use the New Adapter?**

The Adapter allows for quick ruse of existing AI model API's written by Spring.AI.
This will allow any developer familiar with Spring.AI to get a head start to further
get to know and love the unique features that come with ARC.


However there are some *limitations* to the use of Spring.AI models in ARC.
Since they are not written with re-loadability and DSL in mind not all 
key features will work.


**Step-by-Step Guide to Implementing a New Adapter:**

To implement a new adapter for Spring.AI, follow these steps:

1. **Choose an AI Service:** Select the AI model you'd like to integrate
with your application. In this case we will re-implement the natively 
existing [ollama client](blog/Llama3.md).

1. **Get it done:** Since we try to eliminate boilerplate this wont take 
long :).

```kotlin title='chatCompleterProvider for Ollama'
package io.github.lmos.arc.Spring.AI

// Reusing Spring.AI models and terminologies
import org.springframework.ai.ollama.OllamaChatModel
import org.springframework.ai.ollama.api.OllamaApi
import org.springframework.ai.ollama.api.OllamaOptions
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
open class YourApplication {

    @Bean
    open fun chatCompleterProvider(ollamaApi: OllamaApi) = SpringChatClient(
        OllamaChatModel(
            OllamaApi("http://localhost:8888"),
            OllamaOptions.create().withModel("llama3:8b")),
            "llama3:8b",
    )

}
```
**Conclusion:**

Using the new adapter for Spring.AI `SpringChatClient` can greatly reduce
the time of implementation for anyone who has used Spring.AI before. 
By following the before mentioned steps, you can integrate any of the 
Spring.AI models that cater to the specific needs of your applications.

Remember to choose an AI service that aligns with your project's goals and
requirements, and don't hesitate to reach out if you have any questions or
need further guidance on implementing a ARC agent. Happy coding!

:::tip full-potential
Be sure to create a custom implementation of the `ChatCompleter` interface or 
any of the many predefined chatCompleters within the ARC repo to unleash the
full potential of the nimble ARC framework for agent creation.
:::