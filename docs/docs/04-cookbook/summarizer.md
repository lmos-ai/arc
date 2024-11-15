---
sidebar_position: 2
title: Summarizer Agent
---

The Summarizer Agent example highlights the following features:

 - Input Filtering
 - html function
 - extractUrl function

**Summarizer Agent**

The Summarizer Agent summarizes web pages. To achieve this, 
we augment the input message with the content of the web page that the user requests.
This provides the Large Language Model with all the data required to summarize the web page.

In the filterInput block, we use the `extractUrl` function
to extract any web page url from the `inputMessage`.
If a valid url is contained with the input, its content is added to the
`inputMessage`.

In addition, the `html` function is used to filter out the html tags to help reduce
the overall size of the web page document.

```kts
agent {
    name = "summarizer-agent"
    description = "Agent that summarizes web pages."
    prompt {
        """
       You are a helpful agent. 
       You help customers by summarizing webpages. 
       Keep your answer short and concise.
     """
    }
    filterInput {
        val url = extractUrl(inputMessage).firstOrNull()
        if (url != null) {
            debug("Loading url: $url")
            val html = html(url).getOrThrow()
            inputMessage = inputMessage.update("""
                User question: ${inputMessage.content}
                The webpage $url contains the following text:
                $html
             """
            )
        }
    }
}
```

Example question
```
Please summarize the following article: https://www.theregister.com
```

After the web page has been loaded, follow up questions about the web page can be asked.
This is because the content of the web page remains in the transcript that is provided
to the Agent.

The Agent can also be extended to use the `pdf` function to summarize pdf documents.

