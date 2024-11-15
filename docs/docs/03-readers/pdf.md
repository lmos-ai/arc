---
title: PDF Reader
---

Powered by https://pdfbox.apache.org/

The `pdf` function is used to read the content of a pdf file.
Pdf files can be loaded from a local file or a url.

```
https://example.com/file.pdf
file://path/to/file.pdf
```

Example of using the `pdf` function in a Function:
```kts
function(
    name = "get_pdf_data",
    description = "Returns the content of a pdf file.",
    params = types(string("file", "the location of the pdf file."))
) { (file) ->
   pdf(file).getOrNull() ?: "Could not load the pdf file."
}
```

