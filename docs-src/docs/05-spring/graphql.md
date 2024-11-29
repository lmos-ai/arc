
# Agent GraphQL

The Arc GraphQL Spring Boot Starter provides a GraphQL API for interacting with Arc Agents.

(Hint: Checkout the Project at https://github.com/lmos-ai/arc-spring-init for a quick start.)

Once added to a Spring Boot Application, the following endpoints are available:

### Agent Subscription

The `Agent Subscription` endpoint allows you to send a request to an Agent and receive a stream of responses.

The advantage of using a graphql subscription over a standard rest/http endpoint is that the 
websocket connection that is created in the background is more robust against timeouts. 

Furthermore, the subscription enables the Agent to send multiple responses to each request. 
This can be helpful, for example, when the Agent is executing long-running tasks and wants to provide updates to the user.

Example
```graphql
subscription {
    agent(
        agentName: "assistant-agent"
        request: {
            conversationContext: {
                conversationId: "1"
            }
            systemContext: [],
            userContext: {
                userId: "1234",
                profile: [{
                    key: "name",
                    value: "Pat"
                }]
            },
            messages: [
                {
                    role: "user",
                    content: "Hi",
                    format: "text",
                }
            ]
        }
    ) {
        messages {
            content
        }
    }
}
```
See the [Arc API](/docs/11-api.md) for more details on the data models and interfaces.


### Agent Query

The `Agent Query` endpoint returns the list of agents that are active on the server.

Example
```graphql
query {
    agent {
        names
    }
}
```


### Setup

Add the following dependencies to your project:

```kts
implementation("ai.ancf.lmos:arc-api:$arcVersion")
implementation("ai.ancf.lmos:arc-graphql-spring-boot-starter:$arcVersion")
implementation("ai.ancf.lmos:arc-spring-boot-starter:$arcVersion") // recommended
```


### Event Subscriptions 

To better debug the Agents, a further graphql subscription can be enabled that provides access to 
all the events emitted by the Arc application.

```graphql
subscription {
    events {
        type
        payload
        conversationId
    }
}
```

The feature is disabled by default. Add the following to your `application.yml` to enable it:
```yaml
arc:
  subscriptions:
    events:
      enable: true
```

### Arc View

The Arc View is available at `http://localhost:8080/chat/index.html`.

Add the following to your `application.yml` to enable it:
```yaml
arc:
  chat:
    ui:
     enabled: true
  subscriptions:
    events:
      enable: true
```

See the [Arc View](/docs/14-view.md) for more details on the UI.


### Graphiql

The Graphiql UI is available at `http://localhost:8080/graphiql?path=/graphql`.

Add the following to your `application.yml` to enable it:
```yaml
spring:
 graphql:
   graphiql:
    enabled: true
```
