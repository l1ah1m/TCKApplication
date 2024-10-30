package com.messenger.chat

import com.messenger.chat.models.ChatMessage
import com.messenger.chat.repository.ChatRepository
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.Socket
import java.time.format.DateTimeFormatter
import kotlin.concurrent.thread

class Client(private val chatRepository: ChatRepository) {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    fun join() {
        println("Enter the port of the chat to join:")
        println("Created chats:")

        val ports = chatRepository.getActiveSessions()
        for (port in ports) {
            println("$port \n")
        }

        val port = readlnOrNull()?.toIntOrNull()

        if (port != null) {
            try {
                Socket("localhost", port).use { socket ->
                    val writer = OutputStreamWriter(socket.getOutputStream())

                    println("Enter your username:")
                    val username = readlnOrNull() ?: "Unknown"

                    thread {
                        InputStreamReader(socket.getInputStream()).use { reader ->
                            val bufferedReader = reader.buffered()
                            var incomingMessage: String?

                            while (bufferedReader.readLine().also { incomingMessage = it } != null) {
                                println(incomingMessage)
                            }
                        }
                    }

                    writer.write("$username\n")
                    writer.flush()

                    println("Start chatting on port: $port!")

                    while (true) {
                        val message = readLine() ?: continue
                        writer.write("$message\n")
                        writer.flush()
                    }
                }
            } catch (e: Exception) {
                println("Error connecting to the chat: ${e.message}")
            }
        } else {
            println("Invalid port number.")
        }
    }
}
