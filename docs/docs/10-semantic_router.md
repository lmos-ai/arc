---
title: Semantic Router
---

Semantic Routing is a superfast way to route incoming utterances to destinations / agents.

The router uses semantic similarity to match incoming utterances to routes.
For this, the Semantic Router uses a TextEmbedder to create TextEmbeddings
of the incoming utterances and the routes and then compares them using cosine similarity.

The route with the highest similarity is chosen as the destination.

The `SemanticRouter` can be used as a standalone component or as part of a Spring Boot application.

```kotlin
  // Define routes
val routes = semanticRoutes(
    "joke".routeBy(
        "jokes",
        "tell me a joke",
        "i want to hear something funny"
    ),
    "weather".routeBy(
        "how is the weather?",
        "is it sunny?",
        "what's the temperature?",
        "weather"
    )
)
val semanticRouter =
    SemanticRouter(OllamaClient(), routes) // Use an AI Client that implements the TextEmbedder interface.
val result = semanticRouter.route("tell me a joke")
println(result.destination) // equals "joke"
println(result.accuracy) // accuracy of match

// The route can then be used, for example, to get an ARC Agent
val agent = agentProvider.getAgentByName(result.destination) 
```

**Hint**: A routing accuracy of 0.8 or higher is considered a good match.
Routes below that accuracy should probably be discarded.

**Important**: The `SemanticRouter` needs to create the TextEmbeddings of the routes on initialization.
During this time, the `SemanticRouter` is not ready and will return null for all utterances.
If the `SemanticRouter` is ready or not be checked with the `isReady` property.

### Spring Boot Integration

To enable semantic routing in your Spring Boot application, add the following properties to the `application.yml` file.

```yaml
arc:
  router:
    enable: true
    model: "llama3:8b"
```

(Model is the Model of the TextEmbedder you want to use.)

Then simply define the routes as a Bean in your Spring Boot configuration.

```kotlin
@Bean
fun routes() = semanticRoutes(
        "joke".routeBy(
            "jokes",
            "tell me a joke",
            "i want to hear something funny"
        ),
        "weather".routeBy(
            "how is the weather?",
            "is it sunny?",
            "what's the temperature?",
            "weather"
        )
    )
```

After this the `SemanticRouter` is ready to use and can be injected into your components.

### Events

The `SemanticRouter` emits the following events:

| Name                | Description                                                 |   
|---------------------|-------------------------------------------------------------|
| `RouterReadyEvent`  | published when the `SemanticRouter` is ready to use.        |   
| `RouterRoutedEvent` | published when the `SemanticRouter` is routes an utterance. |   

### Metrics

When used in a Spring Boot application with Micrometer,
the `SemanticRouter` emits the following metrics:

| Name                | Type  | Tags                  |   
|---------------------|-------|-----------------------|  
| `arc.router.routed` | Timer | destination, accuracy |  

