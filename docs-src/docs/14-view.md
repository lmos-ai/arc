
# Arc View

The Arc View is a graphical user interface 
that allows you to interact with the Arc Agents.

### Features

- Chat UI to communicate with the Agents.
- Event log to view events emitted by the Arc application.
- Charts to help compare the performance of the Agents.

![Arc View](chat_view.png)

(Event Subscriptions must be enabled in the Arc application to view events,
see [Event Subscriptions](/docs/spring/graphql#event-subscriptions)).

Once enabled, the Arc Events can be displayed in performance charts to better 
asses the performance of the Agents and LLMs.

![Arc View](chart_view.png)

### How to access it
Currently, the Arc View is deployed as part of the Arc GraphQL package. 
Meaning that applications using the package can be configured to serve 
the Arc View as a web application. 

See the [Arc GraphQL](/docs/spring/graphql) for more details.


### Where to find it

The source code for the Arc View is available at [arc-view](https://github.com/lmos-ai/arc-view).

As it is written in Flutter, it can be compiled to also run as a desktop application.

Contributions are welcome!
