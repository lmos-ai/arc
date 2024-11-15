---
title: Html Reader
---

Powered by https://jsoup.org/

The `html` function is used to read the content of html files.
Html files are a popular source of data for AI Agents.
However, the html tags can quickly eat up the limited tokens of prompts.
With the `html` function, html tags are striped away and only the content of the file is returned.

Html files can be loaded from a local file or a url.

```
https://example.com/index.html
file://path/to/index.html
```

Example of using the `html` function in a Function:
```kts
function(
    name = "get_html_data",
    description = "Returns the content of a Html file.",
    params = types(string("file", "the location of the html file."))
) { (file) ->
    html(file).getOrNull() ?: "Could not load the html file."
}
```

**Furthermore**, the `htmlDocument` function can be used to parse the contents of html to a document model
which can be used to select individuals parts of the html file. 

For example:
```kts
val doc = htmlDocument("https://www.telekom.de").getOrThrow()
doc.select("p").text() // retrieves all p tags from the html document
```
(see https://jsoup.org/ for more details)


