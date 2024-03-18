package sh.miles.pineapplebot

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Scanner
import kotlin.system.exitProcess

private val logger = KotlinLogging.logger {}

fun main(args: Array<String>) {
    runBlocking {
        // Start discord bot and load file stuff, yml maybe?
        val bot = PineappleBot()
        launch {
            bot.start()
        }
        // Handle input scanning
        launch {
            val scanner = Scanner(System.`in`)
            while (scanner.hasNext()) {
                val input = scanner.next()
                when(input) {
                    "stop", "shutdown" -> {
                        logger.info { "Shutting down the bot" }
                        bot.shutdown()
                        exitProcess(1)
                    }
                }
            }
        }
    }
}