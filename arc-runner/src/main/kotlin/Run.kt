// SPDX-FileCopyrightText: 2024 Deutsche Telekom AG
//
// SPDX-License-Identifier: Apache-2.0

package ai.ancf.lmos.arc.runner

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.context.ApplicationListener
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.stereotype.Component
import picocli.CommandLine.Command
import picocli.CommandLine.Parameters
import java.awt.Desktop
import java.io.File
import java.io.FileInputStream
import java.net.URI
import java.util.*

@Command(
    name = "run",
    mixinStandardHelpOptions = true,
    description = ["Starts the Arc Runner."],
)
@SpringBootApplication
open class RunArc : Runnable {

    @Parameters(
        index = "0",
        defaultValue = "HOME",
        description = ["The name of the folder containing the agents to run. Defaults to HOME."],
    )
    private var agentFolder: String = ""

    override fun run() {
        println("Staring Arc Runner...")

        val properties = Properties()
        val propertiesFile = File(home(), "arc.properties")
        if (propertiesFile.exists()) {
            properties.load(FileInputStream(propertiesFile))
        }

        val aiKey = System.getenv("ARC_AI_KEY") ?: properties.getProperty("ARC_AI_KEY")
        val aiUrl = System.getenv("ARC_AI_URL") ?: properties.getProperty("ARC_AI_URL")
        if (aiUrl == null && aiKey == null) {
            println("Please set either ARC_AI_URL and ARC_AI_KEY environment variables...")
            return
        }

        val agentsHome = if (agentFolder == "HOME" || agentFolder == "") home() else File(agentFolder)

        properties.put("arc.chat.ui.enabled", "true")
        properties.put("arc.scripts.folder", agentsHome.absolutePath)
        properties.put("arc.scripts.hotReload.enable", "true")
        properties.put("spring.main.banner-mode", "off")
        properties.put("logging.level.root", "WARN")
        properties.put("logging.level.ArcDSL", "DEBUG")
        properties.put("logging.level.ai.ancf.lmos.arc", "DEBUG")

        properties.put("arc.ai.clients[0].id", "GPT-4o")
        properties.put("arc.ai.clients[0].model-name", "GPT-4o")
        properties.put("arc.ai.clients[0].client", "azure")
        if(aiUrl != null) properties.put("arc.ai.clients[0].url", aiUrl)
        if(aiKey != null) properties.put("arc.ai.clients[0].apiKey", aiKey)

        SpringApplicationBuilder(RunArc::class.java).properties(properties).headless(false).build().run()
    }
}

@Component
class StartupApplicationListenerExample : ApplicationListener<ContextRefreshedEvent?> {

    override fun onApplicationEvent(event: ContextRefreshedEvent) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            println("Opening Arc View...")
            try {
                Desktop.getDesktop().browse(URI("http://localhost:8080/chat/index.html"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            println("Open http://localhost:8080/chat/index.html in a browser to chat to Arc...")
        }
    }
}
