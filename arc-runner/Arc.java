// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

//usr/bin/env jbang "$0" "$@" ; exit $?
//JAVA 21

//DEPS ai.ancf.lmos:arc-scripting:0.73.0
//DEPS ai.ancf.lmos:arc-azure-client:0.73.0
//DEPS ai.ancf.lmos:arc-agents:0.73.0
//DEPS ai.ancf.lmos:arc-spring-boot-starter:0.73.0
//DEPS ai.ancf.lmos:arc-spring-boot-starter:0.73.0
//DEPS ai.ancf.lmos:arc-graphql-spring-boot-starter:0.73.0
//DEPS com.graphql-java:graphql-java:21.5
//DEPS com.fasterxml.jackson.core:jackson-databind:2.17.2
//DEPS io.projectreactor:reactor-core:3.6.9
//DEPS ch.qos.logback:logback-classic:1.5.7
//DEPS org.slf4j:slf4j-api:2.0.16
//DEPS org.slf4j:slf4j-simple:2.0.1
//DEPS com.azure:azure-identity:1.13.1

package arc.runner;

import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Properties;

import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("arc.chat.ui.enabled", "true");
        properties.put("arc.scripts.folder", "./agents");
        properties.put("arc.scripts.hotReload.enable", "true");

        new SpringApplicationBuilder(Application.class)
                .properties(properties)
                .build()
                .run(args);
    }
}