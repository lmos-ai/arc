"use strict";(self.webpackChunkarc=self.webpackChunkarc||[]).push([[1419],{4948:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>s,contentTitle:()=>l,default:()=>h,frontMatter:()=>r,metadata:()=>i,toc:()=>p});var i=t(4573),a=t(4848),o=t(8453);const r={slug:"Spring.AI-integration",title:"Sptring.ai integration using the SpringChatClient",authors:[{name:"Max"}],tags:["spring","Spring.AI","feature"]},l=void 0,s={authorsImageUrls:[void 0]},p=[];function c(e){const n={a:"a",admonition:"admonition",code:"code",em:"em",li:"li",ol:"ol",p:"p",pre:"pre",strong:"strong",...(0,o.R)(),...e.components};return(0,a.jsxs)(a.Fragment,{children:[(0,a.jsx)(n.p,{children:"As the world of AI continues to evolve and develop,\nthe need of integration of AI services into your applications\nhas become increasingly valuable."}),"\n",(0,a.jsx)(n.p,{children:"Since ARC's goal is to make the process of integration, management\nand creation of AI in your existing services as seamless as possible\nit only made sense to integrate Spring.AI to work in a plug and play\nfashion."}),"\n",(0,a.jsx)(n.p,{children:"In this blog post, we'll explore how to implement a new adapter for\nSpring.AI to work within ARC.\nTo expand its capabilities and make it easier to incorporate various\nAI services into your projects."}),"\n",(0,a.jsx)(n.p,{children:(0,a.jsxs)(n.strong,{children:["What is an the ",(0,a.jsx)(n.code,{children:"SpringChatClient"})," Adapter in ARC?"]})}),"\n",(0,a.jsxs)(n.p,{children:["In ARC, the ",(0,a.jsx)(n.code,{children:"SpringChatClient"})," acts like a bridge between the framework and\nexternal Spring.AI models/ APIs. It enables you to seamlessly integrate any\nSpring.AI ChatModels in your ARC application, allowing for easy access to their\nfunctionality. These Adapters/ ChatClients can be used to connect to various\nAI platforms, such as Google VertexAI, Amazon Comprehend, Grog, Mistral.Ai,\nIBM Watson or many ",(0,a.jsx)(n.a,{href:"https://docs.spring.io/spring-ai/reference/api/chatmodel.html",children:"more"}),"."]}),"\n",(0,a.jsx)(n.p,{children:(0,a.jsx)(n.strong,{children:"Why use the New Adapter?"})}),"\n",(0,a.jsx)(n.p,{children:"The Adapter allows for quick ruse of existing AI model API's written by Spring.AI.\nThis will allow any developer familiar with Spring.AI to get a head start to further\nget to know and love the unique features that come with ARC."}),"\n",(0,a.jsxs)(n.p,{children:["However there are some ",(0,a.jsx)(n.em,{children:"limitations"})," to the use of Spring.AI models in ARC.\nSince they are not written with re-loadability and DSL in mind not all\nkey features will work."]}),"\n",(0,a.jsx)(n.p,{children:(0,a.jsx)(n.strong,{children:"Step-by-Step Guide to Implementing a New Adapter:"})}),"\n",(0,a.jsx)(n.p,{children:"To implement a new adapter for Spring.AI, follow these steps:"}),"\n",(0,a.jsxs)(n.ol,{children:["\n",(0,a.jsxs)(n.li,{children:["\n",(0,a.jsxs)(n.p,{children:[(0,a.jsx)(n.strong,{children:"Choose an AI Service:"})," Select the AI model you'd like to integrate\nwith your application. In this case we will re-implement the natively\nexisting ",(0,a.jsx)(n.a,{href:"/arc/blog/Llama3",children:"ollama client"}),"."]}),"\n"]}),"\n",(0,a.jsxs)(n.li,{children:["\n",(0,a.jsxs)(n.p,{children:[(0,a.jsx)(n.strong,{children:"Get it done:"})," Since we try to eliminate boilerplate this wont take\nlong :)."]}),"\n"]}),"\n"]}),"\n",(0,a.jsx)(n.pre,{children:(0,a.jsx)(n.code,{className:"language-kotlin",metastring:"title='chatCompleterProvider for Ollama'",children:'package io.github.lmos.arc.Spring.AI\n\n// Reusing Spring.AI models and terminologies\nimport org.springframework.ai.ollama.OllamaChatModel\nimport org.springframework.ai.ollama.api.OllamaApi\nimport org.springframework.ai.ollama.api.OllamaOptions\nimport org.springframework.boot.autoconfigure.SpringBootApplication\nimport org.springframework.context.annotation.Bean\n\n@SpringBootApplication\nopen class YourApplication {\n\n    @Bean\n    open fun chatCompleterProvider(ollamaApi: OllamaApi) = SpringChatClient(\n        OllamaChatModel(\n            OllamaApi("http://localhost:8888"),\n            OllamaOptions.create().withModel("llama3:8b")),\n            "llama3:8b",\n    )\n\n}\n'})}),"\n",(0,a.jsx)(n.p,{children:(0,a.jsx)(n.strong,{children:"Conclusion:"})}),"\n",(0,a.jsxs)(n.p,{children:["Using the new adapter for Spring.AI ",(0,a.jsx)(n.code,{children:"SpringChatClient"})," can greatly reduce\nthe time of implementation for anyone who has used Spring.AI before.\nBy following the before mentioned steps, you can integrate any of the\nSpring.AI models that cater to the specific needs of your applications."]}),"\n",(0,a.jsx)(n.p,{children:"Remember to choose an AI service that aligns with your project's goals and\nrequirements, and don't hesitate to reach out if you have any questions or\nneed further guidance on implementing a ARC agent. Happy coding!"}),"\n",(0,a.jsx)(n.admonition,{title:"full-potential",type:"tip",children:(0,a.jsxs)(n.p,{children:["Be sure to create a custom implementation of the ",(0,a.jsx)(n.code,{children:"ChatCompleter"})," interface or\nany of the many predefined chatCompleters within the ARC repo to unleash the\nfull potential of the nimble ARC framework for agent creation."]})})]})}function h(e={}){const{wrapper:n}={...(0,o.R)(),...e.components};return n?(0,a.jsx)(n,{...e,children:(0,a.jsx)(c,{...e})}):c(e)}},8453:(e,n,t)=>{t.d(n,{R:()=>r,x:()=>l});var i=t(6540);const a={},o=i.createContext(a);function r(e){const n=i.useContext(o);return i.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function l(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(a):e.components||a:r(e.components),i.createElement(o.Provider,{value:n},e.children)}},4573:e=>{e.exports=JSON.parse('{"permalink":"/arc/blog/Spring.AI-integration","source":"@site/blog/SpringAiClient.md","title":"Sptring.ai integration using the SpringChatClient","description":"As the world of AI continues to evolve and develop,","date":"2024-11-15T08:36:03.000Z","tags":[{"inline":true,"label":"spring","permalink":"/arc/blog/tags/spring"},{"inline":true,"label":"Spring.AI","permalink":"/arc/blog/tags/spring-ai"},{"inline":true,"label":"feature","permalink":"/arc/blog/tags/feature"}],"readingTime":2.3,"hasTruncateMarker":false,"authors":[{"name":"Max","socials":{},"key":null,"page":null}],"frontMatter":{"slug":"Spring.AI-integration","title":"Sptring.ai integration using the SpringChatClient","authors":[{"name":"Max"}],"tags":["spring","Spring.AI","feature"]},"unlisted":false,"prevItem":{"title":"Llama3 is out!","permalink":"/arc/blog/Llama3"}}')}}]);