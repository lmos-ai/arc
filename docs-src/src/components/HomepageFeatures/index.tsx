/*
 * SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import clsx from 'clsx';
import Heading from '@theme/Heading';
import styles from './styles.module.css';

type FeatureItem = {
  title: string;
  description: JSX.Element;
};

const FeatureList: FeatureItem[] = [
  {
    title: 'Blazing Fast Development',
    description: (
      <>
          Our design prioritizes rapid prototyping with quick feedback loops for agents & tools development. 
        
          Designed ground up for seamless collaboration between engineers and data scientists.
      </>
    ),
  },
  {
    title: 'Delightfully Simple',
    description: (
      <>
          A delightfully simple yet powerful new language for developing agents for programmers of all backgrounds and skill levels.
Focus on what matters. Leave the jargons, bloated abstractions behind.
Powerful extension hooks unleash your full coding potential for flexibility.

      </>
    ),
  },
  {
    title: 'Production grade out of the box',
    description: (
      <>
         Build AI agents that can handle real-world demands. Built on the robust & reliable JVM combined with the concurrency constructs in Kotlin offer best in class performance and scalability out of the box. 

      </>
    ),
  },
];

function Feature({title, description}: FeatureItem) {
  return (
    <div className={clsx('col col--4')}>
      <div className="text--center">
      </div>
      <div className="text--center padding-horiz--md">
        <Heading as="h3">{title}</Heading>
        <p>{description}</p>
      </div>
    </div>
  );
}

export default function HomepageFeatures(): JSX.Element {
  return (
    <section className={styles.features}>
      <div className="container">
        <div className="row">
          {FeatureList.map((props, idx) => (
            <Feature key={idx} {...props} />
          ))}
        </div>
      </div>
    </section>
  );
}
