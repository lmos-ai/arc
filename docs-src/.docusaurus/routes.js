import ComponentCreator from '@docusaurus/ComponentCreator';

export default [
  {
    path: '/arc/__docusaurus/debug',
    component: ComponentCreator('/arc/__docusaurus/debug', 'cae'),
    exact: true
  },
  {
    path: '/arc/__docusaurus/debug/config',
    component: ComponentCreator('/arc/__docusaurus/debug/config', '832'),
    exact: true
  },
  {
    path: '/arc/__docusaurus/debug/content',
    component: ComponentCreator('/arc/__docusaurus/debug/content', 'bb6'),
    exact: true
  },
  {
    path: '/arc/__docusaurus/debug/globalData',
    component: ComponentCreator('/arc/__docusaurus/debug/globalData', '5f2'),
    exact: true
  },
  {
    path: '/arc/__docusaurus/debug/metadata',
    component: ComponentCreator('/arc/__docusaurus/debug/metadata', 'b4d'),
    exact: true
  },
  {
    path: '/arc/__docusaurus/debug/registry',
    component: ComponentCreator('/arc/__docusaurus/debug/registry', 'fe3'),
    exact: true
  },
  {
    path: '/arc/__docusaurus/debug/routes',
    component: ComponentCreator('/arc/__docusaurus/debug/routes', '07f'),
    exact: true
  },
  {
    path: '/arc/blog',
    component: ComponentCreator('/arc/blog', '3a6'),
    exact: true
  },
  {
    path: '/arc/blog/archive',
    component: ComponentCreator('/arc/blog/archive', 'b45'),
    exact: true
  },
  {
    path: '/arc/blog/Llama3',
    component: ComponentCreator('/arc/blog/Llama3', '895'),
    exact: true
  },
  {
    path: '/arc/blog/Spring.AI-integration',
    component: ComponentCreator('/arc/blog/Spring.AI-integration', 'bad'),
    exact: true
  },
  {
    path: '/arc/blog/tags',
    component: ComponentCreator('/arc/blog/tags', '006'),
    exact: true
  },
  {
    path: '/arc/blog/tags/feature',
    component: ComponentCreator('/arc/blog/tags/feature', 'ce5'),
    exact: true
  },
  {
    path: '/arc/blog/tags/spring',
    component: ComponentCreator('/arc/blog/tags/spring', '335'),
    exact: true
  },
  {
    path: '/arc/blog/tags/spring-ai',
    component: ComponentCreator('/arc/blog/tags/spring-ai', 'a62'),
    exact: true
  },
  {
    path: '/arc/markdown-page',
    component: ComponentCreator('/arc/markdown-page', 'a54'),
    exact: true
  },
  {
    path: '/arc/docs',
    component: ComponentCreator('/arc/docs', '07c'),
    routes: [
      {
        path: '/arc/docs',
        component: ComponentCreator('/arc/docs', '775'),
        routes: [
          {
            path: '/arc/docs',
            component: ComponentCreator('/arc/docs', 'ea0'),
            routes: [
              {
                path: '/arc/docs/api',
                component: ComponentCreator('/arc/docs/api', '14d'),
                exact: true,
                sidebar: "tutorialSidebar"
              },
              {
                path: '/arc/docs/cli',
                component: ComponentCreator('/arc/docs/cli', 'b42'),
                exact: true,
                sidebar: "tutorialSidebar"
              },
              {
                path: '/arc/docs/clients/',
                component: ComponentCreator('/arc/docs/clients/', 'e30'),
                exact: true,
                sidebar: "tutorialSidebar"
              },
              {
                path: '/arc/docs/clients/azure',
                component: ComponentCreator('/arc/docs/clients/azure', '365'),
                exact: true,
                sidebar: "tutorialSidebar"
              },
              {
                path: '/arc/docs/clients/gemini',
                component: ComponentCreator('/arc/docs/clients/gemini', 'fea'),
                exact: true,
                sidebar: "tutorialSidebar"
              },
              {
                path: '/arc/docs/clients/langchain4j',
                component: ComponentCreator('/arc/docs/clients/langchain4j', '727'),
                exact: true,
                sidebar: "tutorialSidebar"
              },
              {
                path: '/arc/docs/clients/ollama',
                component: ComponentCreator('/arc/docs/clients/ollama', '70b'),
                exact: true,
                sidebar: "tutorialSidebar"
              },
              {
                path: '/arc/docs/clients/springai',
                component: ComponentCreator('/arc/docs/clients/springai', '821'),
                exact: true,
                sidebar: "tutorialSidebar"
              },
              {
                path: '/arc/docs/cookbook/',
                component: ComponentCreator('/arc/docs/cookbook/', '803'),
                exact: true,
                sidebar: "tutorialSidebar"
              },
              {
                path: '/arc/docs/cookbook/summarizer',
                component: ComponentCreator('/arc/docs/cookbook/summarizer', 'df8'),
                exact: true,
                sidebar: "tutorialSidebar"
              },
              {
                path: '/arc/docs/cookbook/weather',
                component: ComponentCreator('/arc/docs/cookbook/weather', '9de'),
                exact: true,
                sidebar: "tutorialSidebar"
              },
              {
                path: '/arc/docs/dsl/',
                component: ComponentCreator('/arc/docs/dsl/', '9c4'),
                exact: true,
                sidebar: "tutorialSidebar"
              },
              {
                path: '/arc/docs/dsl/accessing_beans',
                component: ComponentCreator('/arc/docs/dsl/accessing_beans', 'b4b'),
                exact: true,
                sidebar: "tutorialSidebar"
              },
              {
                path: '/arc/docs/dsl/defining_agents',
                component: ComponentCreator('/arc/docs/dsl/defining_agents', '760'),
                exact: true,
                sidebar: "tutorialSidebar"
              },
              {
                path: '/arc/docs/dsl/defining_functions',
                component: ComponentCreator('/arc/docs/dsl/defining_functions', 'a15'),
                exact: true,
                sidebar: "tutorialSidebar"
              },
              {
                path: '/arc/docs/dsl/extensions',
                component: ComponentCreator('/arc/docs/dsl/extensions', '793'),
                exact: true,
                sidebar: "tutorialSidebar"
              },
              {
                path: '/arc/docs/dsl/filters',
                component: ComponentCreator('/arc/docs/dsl/filters', 'd38'),
                exact: true,
                sidebar: "tutorialSidebar"
              },
              {
                path: '/arc/docs/eventing/',
                component: ComponentCreator('/arc/docs/eventing/', 'b40'),
                exact: true,
                sidebar: "tutorialSidebar"
              },
              {
                path: '/arc/docs/graphql',
                component: ComponentCreator('/arc/docs/graphql', '1f3'),
                exact: true,
                sidebar: "tutorialSidebar"
              },
              {
                path: '/arc/docs/index',
                component: ComponentCreator('/arc/docs/index', 'b80'),
                exact: true,
                sidebar: "tutorialSidebar"
              },
              {
                path: '/arc/docs/langchain',
                component: ComponentCreator('/arc/docs/langchain', '4bd'),
                exact: true,
                sidebar: "tutorialSidebar"
              },
              {
                path: '/arc/docs/manual_setup',
                component: ComponentCreator('/arc/docs/manual_setup', '1bd'),
                exact: true,
                sidebar: "tutorialSidebar"
              },
              {
                path: '/arc/docs/memory/',
                component: ComponentCreator('/arc/docs/memory/', '29f'),
                exact: true,
                sidebar: "tutorialSidebar"
              },
              {
                path: '/arc/docs/packages',
                component: ComponentCreator('/arc/docs/packages', '290'),
                exact: true,
                sidebar: "tutorialSidebar"
              },
              {
                path: '/arc/docs/quickstart',
                component: ComponentCreator('/arc/docs/quickstart', 'f30'),
                exact: true,
                sidebar: "tutorialSidebar"
              },
              {
                path: '/arc/docs/readers/',
                component: ComponentCreator('/arc/docs/readers/', '762'),
                exact: true,
                sidebar: "tutorialSidebar"
              },
              {
                path: '/arc/docs/readers/html',
                component: ComponentCreator('/arc/docs/readers/html', '5c3'),
                exact: true,
                sidebar: "tutorialSidebar"
              },
              {
                path: '/arc/docs/readers/pdf',
                component: ComponentCreator('/arc/docs/readers/pdf', 'f15'),
                exact: true,
                sidebar: "tutorialSidebar"
              },
              {
                path: '/arc/docs/semantic_router',
                component: ComponentCreator('/arc/docs/semantic_router', 'e4d'),
                exact: true,
                sidebar: "tutorialSidebar"
              },
              {
                path: '/arc/docs/spring/',
                component: ComponentCreator('/arc/docs/spring/', '00a'),
                exact: true,
                sidebar: "tutorialSidebar"
              },
              {
                path: '/arc/docs/spring/agent-beans',
                component: ComponentCreator('/arc/docs/spring/agent-beans', 'da2'),
                exact: true,
                sidebar: "tutorialSidebar"
              },
              {
                path: '/arc/docs/spring/integration',
                component: ComponentCreator('/arc/docs/spring/integration', '89f'),
                exact: true,
                sidebar: "tutorialSidebar"
              },
              {
                path: '/arc/docs/spring/metrics',
                component: ComponentCreator('/arc/docs/spring/metrics', 'c17'),
                exact: true,
                sidebar: "tutorialSidebar"
              },
              {
                path: '/arc/docs/view',
                component: ComponentCreator('/arc/docs/view', 'd04'),
                exact: true,
                sidebar: "tutorialSidebar"
              }
            ]
          }
        ]
      }
    ]
  },
  {
    path: '/arc/',
    component: ComponentCreator('/arc/', '953'),
    exact: true
  },
  {
    path: '*',
    component: ComponentCreator('*'),
  },
];
