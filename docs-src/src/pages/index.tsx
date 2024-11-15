/*
 * SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import React from 'react';
import clsx from 'clsx';
import Link from '@docusaurus/Link';
import useDocusaurusContext from '@docusaurus/useDocusaurusContext';
import Layout from '@theme/Layout';
import HomepageFeatures from '@site/src/components/HomepageFeatures';
import Heading from '@theme/Heading';
import styles from './index.module.css';
import Head from '@docusaurus/Head';

function HomepageHeader() {
    const {siteConfig} = useDocusaurusContext();
    return (
        <>
            <header className={clsx('hero', styles.heroBanner)}>
                <div className="container">
                    <Heading as="h1" className="hero__title">
                        <Head>
                            <title>ARC: Agents Reactor</title>
                            <meta property="og:description"
                                  content="A nimble framework & DSL for building LLM powered AI Agents."/>
                        </Head>
                        <span className="gradient-text">
                            <span className="arc-text-underline"><span className="arc-text">A</span></span>
                            <span className="arc-text-complete">gents </span>
                            <span className="arc-text-underline"><span className="arc-text">R</span>
                            </span><span className='arc-text-complete'>ea</span>
                            <span className="arc-text-underline"><span className="arc-text">C</span></span>
                            <span className='arc-text-complete'>tor</span><br/>
                            Build AI Agents.<br/> Faster. Together.<br/>
                            <Link className="button button--primary button--lg" to="/docs/quickstart">
                                My First Agent - 5min ⏱️
                            </Link>
                        </span>
                        <div>
                            <img height={300} className={"code"} src={require('@site/static/img/arc.png').default}
                                 alt='code snippet'/>
                        </div>


                    </Heading>
                    <p className="hero__subtitle"></p>
                    <div className={styles.buttons}></div>
                    <hr/>
                </div>
            </header>
        </>
    );
}

export default function Home(): JSX.Element {
    const {siteConfig} = useDocusaurusContext();
    return (
        <Layout
            title={`Hello from ${siteConfig.title}`}
            description="Description will go into a meta tag in <head />">
            <HomepageHeader/>
            <main>
                <HomepageFeatures/>
                <div className={"arc-row"}>
                    <div style={{"width": "400px"}}>
                        <h3>Easy Chat with the Arc View</h3>
                        <p>
                            Start chatting with your new AI Agent straight away using the built-in Chat interface.
                            Check out the
                            Spring Boot Starter project at <a href={"https://github.com/lmos-ai/arc-spring-init"}>Arc
                            Spring Starter</a> to get up and running in minutes.
                        </p>
                    </div>
                    <div>
                        <img src={"img/chat_view.png"} style={{height: "400px", borderRadius: "10px"}}
                             alt={"Screenshot of the Arc View UI."}/>
                    </div>
                </div>
                <div className={"arc-row"}>
                    <div style={{"width": "400px"}}>
                        <h3>Understand your Agent's Performance</h3>
                        <p>
                           Get deeper insights into your Agent's performance during local testing and evaluation
                            using the performance page.
                        </p>
                        <p>
                          Compare models and prompts before deploying to production.
                        </p>
                    </div>
                    <div>
                        <img src={"img/chart_view.png"} style={{height: "400px", borderRadius: "10px"}}
                             alt={"Screenshot of the Arc View UI."}/>
                    </div>
                </div>
            </main>
        </Layout>
    );
}

