"use strict";(self.webpackChunkarc=self.webpackChunkarc||[]).push([[9213],{5478:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>l,contentTitle:()=>s,default:()=>p,frontMatter:()=>i,metadata:()=>o,toc:()=>c});const o=JSON.parse('{"id":"manual_setup","title":"Manual Setup","description":"Usually the Arc Agent Framework will be used with the Spring Boot Starter","source":"@site/docs/00-manual_setup.md","sourceDirName":".","slug":"/manual_setup","permalink":"/arc/docs/manual_setup","draft":false,"unlisted":false,"tags":[],"version":"current","sidebarPosition":2,"frontMatter":{"title":"Manual Setup","sidebar_position":2},"sidebar":"tutorialSidebar","previous":{"title":"Component Overview","permalink":"/arc/docs/component_overview"},"next":{"title":"AI Clients","permalink":"/arc/docs/clients/"}}');var r=t(4848),a=t(8453);const i={title:"Manual Setup",sidebar_position:2},s=void 0,l={},c=[{value:"Loading Agents",id:"loading-agents",level:3},{value:"Loading Scripted Agents",id:"loading-scripted-agents",level:3},{value:"Executing Agents",id:"executing-agents",level:3}];function d(e){const n={a:"a",blockquote:"blockquote",code:"code",h3:"h3",p:"p",pre:"pre",...(0,a.R)(),...e.components};return(0,r.jsxs)(r.Fragment,{children:[(0,r.jsx)(n.p,{children:"Usually the Arc Agent Framework will be used with the Spring Boot Starter\nor the Arc Runner, which will automatically set up the framework for you."}),"\n",(0,r.jsx)(n.p,{children:"However, if you want to set up the framework manually and use it in a different frameworks or environments,\nyou can do so by following the steps below."}),"\n",(0,r.jsxs)(n.blockquote,{children:["\n",(0,r.jsxs)(n.p,{children:["Also: read the ",(0,r.jsx)(n.a,{href:"/docs/component_overview",children:"Component Overview"})," page for a better understanding of core components of the Framework."]}),"\n"]}),"\n",(0,r.jsx)(n.h3,{id:"loading-agents",children:"Loading Agents"}),"\n",(0,r.jsxs)(n.p,{children:["The ",(0,r.jsx)(n.code,{children:"DSLAgents"})," is a convenient way to load Agents that are defined with Kotlin DSL."]}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:' val chatCompleterProvider = { AzureAIClient(...) } // or any other AIClient\n\n val agentBuilder = DSLAgents.init(chatCompleterProvider).apply {\n    define {\n        agent {\n            name = "agent"\n            description = "agent description"\n            prompt { "Agent prompt goes here." }\n        }\n    }\n\n    defineFunctions {\n        function(\n            name = "get_weather",\n            description = "the weather service",\n            params = types(string("location", "the location")),\n        ) {\n            httpGet("https://api.weather.com/$location")\n        }\n    }\n}\n\nval agents = agentBuilder.getAgents()\n\n'})}),"\n",(0,r.jsx)(n.h3,{id:"loading-scripted-agents",children:"Loading Scripted Agents"}),"\n",(0,r.jsxs)(n.p,{children:["The ",(0,r.jsx)(n.code,{children:"DSLScriptAgents"})," class can be used to load Agents that are defined with kotlin scripts."]}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:'val chatCompleterProvider = { AzureAIClient(...) } // or any other AIClient\n\n val agentBuilder = DSLScriptAgents.init(chatCompleterProvider).apply {\n    define(\n        """\n            agent {\n                name = "agent"\n                description = "agent description"\n                prompt { "Agent prompt goes here." }\n            }\n        """,\n    ).getOrThrow()\n\n    defineFunctions(\n        """\n            function(\n                name = "get_weather",\n                description = "the weather service",\n                params = types(string("location", "the location")),\n            ) { location ->\n               httpGet("https://api.weather.com/${"$"}location")\n            }\n        """,\n    )\n}\n\nval agents = agentBuilder.getAgents()\n\n'})}),"\n",(0,r.jsx)(n.h3,{id:"executing-agents",children:"Executing Agents"}),"\n",(0,r.jsxs)(n.p,{children:["Once an Agent is loaded, it can be executed by passing a ",(0,r.jsx)(n.code,{children:"Conversation"})," object to the ",(0,r.jsx)(n.code,{children:"execute"})," method."]}),"\n",(0,r.jsx)(n.pre,{children:(0,r.jsx)(n.code,{className:"language-kotlin",children:' val agent = agentBuilder.getAgentByName(agentName) as ChatAgent? ?: error("Agent not found!")\n val conversation = Conversation(User("userOrClientId")) + UserMessage("My question")\n val result = agent.execute(conversation).getOrNull()\n'})})]})}function p(e={}){const{wrapper:n}={...(0,a.R)(),...e.components};return n?(0,r.jsx)(n,{...e,children:(0,r.jsx)(d,{...e})}):d(e)}},8453:(e,n,t)=>{t.d(n,{R:()=>i,x:()=>s});var o=t(6540);const r={},a=o.createContext(r);function i(e){const n=o.useContext(a);return o.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function s(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(r):e.components||r:i(e.components),o.createElement(a.Provider,{value:n},e.children)}}}]);