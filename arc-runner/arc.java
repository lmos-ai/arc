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
//DEPS com.azure:azure-identity:1.13.1
//DEPS info.picocli:picocli:4.6.3

package arc.runner;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.Properties;
import org.springframework.boot.builder.SpringApplicationBuilder;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@Command(name = "arc",
        mixinStandardHelpOptions = true,
        version = "1.0.0",
        description = "The Arc Runner")
@SpringBootApplication
public class arc {

    public static void main(String[] args) throws Exception {
        File home = new File(System.getProperty("user.home"), ".arc");
        home.mkdirs();

        if(args.length > 0 && args[0].equals("install")) {
            if(args.length < 1) {
                System.out.println("Please provide the name of the Agent you would like to install...");
                return;
            }
            String agent = args[1];
            if(agent.contains(".") || agent.contains("/")) {
                System.out.println("Invalid Agent name. Please provide a valid Agent name without '.' or '/'...");
                return;
            }
            System.out.println("Installing Arc Runner...");
            String fullName = agent + ".agent.kts";
            InputStream in = new URL("https://raw.githubusercontent.com/lmos-ai/arc/main/arc-runner/" + fullName).openStream();
            Files.copy(in, new File(home, fullName).toPath(), StandardCopyOption.REPLACE_EXISTING);
            return;
        }

        Properties properties = new Properties();
        properties.put("arc.chat.ui.enabled", "true");
        properties.put("arc.scripts.folder", home.getAbsolutePath());
        properties.put("arc.scripts.hotReload.enable", "true");
        properties.put("spring.main.banner-mode", "off");
        properties.put("logging.level.root", "WARN");
        properties.put("logging.level.ArcDSL", "DEBUG");
        properties.put("logging.level.ai.ancf.lmos.arc", "DEBUG");

        properties.put("arc.ai.clients[0].id", "GPT-4o");
        properties.put("arc.ai.clients[0].model-name", "GPT-4o");
        properties.put("arc.ai.clients[0].client", "azure");
        properties.put("arc.ai.clients[0].url", System.getenv("ARC_AI_URL"));

        new SpringApplicationBuilder(arc.class).properties(properties).build().run(args);
    }
}