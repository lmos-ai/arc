"use strict";(self.webpackChunkarc=self.webpackChunkarc||[]).push([[2204],{4990:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>c,contentTitle:()=>a,default:()=>p,frontMatter:()=>l,metadata:()=>r,toc:()=>o});const r=JSON.parse('{"id":"plugin","title":"Arc Gradle Plugin","description":"Experimental","source":"@site/docs/18-plugin.md","sourceDirName":".","slug":"/plugin","permalink":"/arc/docs/plugin","draft":false,"unlisted":false,"tags":[],"version":"current","sidebarPosition":18,"frontMatter":{},"sidebar":"tutorialSidebar","previous":{"title":"Arc CLI","permalink":"/arc/docs/cli"},"next":{"title":"Use Case Prompting","permalink":"/arc/docs/use_cases"}}');var i=t(4848),s=t(8453);const l={},a="Arc Gradle Plugin",c={},o=[];function d(e){const n={code:"code",h1:"h1",header:"header",li:"li",p:"p",pre:"pre",strong:"strong",ul:"ul",...(0,s.R)(),...e.components};return(0,i.jsxs)(i.Fragment,{children:[(0,i.jsx)(n.header,{children:(0,i.jsx)(n.h1,{id:"arc-gradle-plugin",children:"Arc Gradle Plugin"})}),"\n",(0,i.jsx)(n.p,{children:(0,i.jsx)(n.strong,{children:"Experimental"})}),"\n",(0,i.jsx)(n.p,{children:"The Arc Gradle Plugin transpiles Arc Agents defined in Kotlin script files to plain kotlin code that\nis then compiled into a jar file."}),"\n",(0,i.jsxs)(n.p,{children:[(0,i.jsx)(n.strong,{children:"Note:"}),' The Agent scripts must reside in the folder "./agents" in the project root.']}),"\n",(0,i.jsxs)(n.p,{children:["Simply add the following to your ",(0,i.jsx)(n.code,{children:"build.gradle.kts"})," file to enable the plugin:"]}),"\n",(0,i.jsx)(n.pre,{children:(0,i.jsx)(n.code,{className:"language-kotlin",children:'plugins {\n    id("ai.ancf.lmos.arc.gradle.plugin") version "0.115.0"\n}\n'})}),"\n",(0,i.jsx)(n.p,{children:"Once the plugin is enabled, Arc Agents will be automatically built along with the rest of the project and\nare available as:"}),"\n",(0,i.jsxs)(n.ul,{children:["\n",(0,i.jsx)(n.li,{children:"package ai.ancf.lmos.arc.agents.gen.Agents"}),"\n",(0,i.jsx)(n.li,{children:"package ai.ancf.lmos.arc.agents.gen.Functions"}),"\n"]})]})}function p(e={}){const{wrapper:n}={...(0,s.R)(),...e.components};return n?(0,i.jsx)(n,{...e,children:(0,i.jsx)(d,{...e})}):d(e)}},8453:(e,n,t)=>{t.d(n,{R:()=>l,x:()=>a});var r=t(6540);const i={},s=r.createContext(i);function l(e){const n=r.useContext(s);return r.useMemo((function(){return"function"==typeof e?e(n):{...n,...e}}),[n,e])}function a(e){let n;return n=e.disableParentContext?"function"==typeof e.components?e.components(i):e.components||i:l(e.components),r.createElement(s.Provider,{value:n},e.children)}}}]);