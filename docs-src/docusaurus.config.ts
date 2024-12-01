// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

import {themes as prismThemes} from 'prism-react-renderer';
import type {Config} from '@docusaurus/types';
import type * as Preset from '@docusaurus/preset-classic';

const config: Config = {
    title: 'Arc',
    tagline: 'Build AI Agents.<br/> Faster. Together.',
    favicon: 'img/favicon.ico',

    // Set the production url of your site here
    url: 'https://lmos-ai.github.io/',
    // Set the /<baseUrl>/ pathname under which your site is served
    // For GitHub pages deployment, it is often '/<projectName>/'
    baseUrl: '/arc',

    // GitHub pages deployment config.
    // If you aren't using GitHub pages, you don't need these.
    organizationName: 'Deutsche Telekom AG', // Usually your GitHub org/user name.
    projectName: 'Arc', // Usually your repo name.

    onBrokenLinks: 'throw',
    onBrokenMarkdownLinks: 'warn',

    // Even if you don't use internationalization, you can use this field to set
    // useful metadata like html lang. For example, if your site is Chinese, you
    // may want to replace "en" with "zh-Hans".
    i18n: {
        defaultLocale: 'en',
        locales: ['en'],
    },

    presets: [
        [
            'classic',
            {
                docs: {
                    sidebarPath: './sidebars.ts',

                },
                blog: {
                    showReadingTime: true,

                },
                theme: {
                    customCss: './src/css/custom.css',
                },
            } satisfies Preset.Options,
        ],
    ],

    themeConfig: {
        // Replace with your project's social card
        image: 'img/social-card.png',
        navbar: {
            // title: 'Arc',
            //  logo: {
            //   alt: 'My Site Logo',
            //   src: 'img/logo111.png',
            //      width:'42px',
            //      height:'42px'
            // },
            items: [
                {
                    type: 'docSidebar',
                    sidebarId: 'tutorialSidebar',
                    position: 'left',
                    label: 'Docs',
                },
                {to: '/docs/cookbook', label: 'Cookbook', position: 'left'},
                // {to: '/blog', label: 'Blog', position: 'left'},
                {
                    href: 'https://github.com/lmos-ai/arc/releases',
                    label: '0.114.0',
                    position: 'right',
                },  {
                    href: 'https://github.com/lmos-ai/arc',
                    label: 'GitHub',
                    position: 'right',
                },

            ],
        },
        footer: {
            style: 'dark',
            links: [
                {},
                {},   //empty objects added to move the column to the right
                {},
                {},
                {},
                {

                    items: [

                        {
                            href: 'https://www.telekom.com/imprint',
                            label: 'Imprint',
                            position: 'right',

                        },
                        {
                            href: 'https://carbon.now.sh/',
                            label: 'Carbon',
                            position: 'right',

                        },

                    ],
                },
            ],
            copyright: `Copyright © ${new Date().getFullYear()} Deutsche Telekom AG, Inc. Built with Docusaurus.`,
        },
        prism: {
            theme: prismThemes.github,
            darkTheme: prismThemes.dracula,
        },
    } satisfies Preset.ThemeConfig,
};

export default config;
