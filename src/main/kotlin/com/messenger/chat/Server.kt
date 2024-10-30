package com.messenger.chat

import com.messenger.chat.models.ChatMessage
import com.messenger.chat.repository.ChatRepository
import java.io.OutputStreamWriter
import java.net.ServerSocket
import java.net.Socket
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.concurrent.thread

class Server(private val chatRepository: ChatRepository) {
    private val clients = mutableListOf<OutputStreamWriter>()
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

    fun startAllActiveSessions() {
        val activeSessions = chatRepository.getActiveSessions()

        activeSessions.forEach { port ->
            try {
                val serverSocket = ServerSocket(port)
                println("Chat started on port $port")

                thread {
                    try {
                        while (true) {
                            val clientSocket = serverSocket.accept()
                            handleClient(clientSocket, port)
                        }
                    } catch (e: Exception) {
                        println("Error while accepting client connections: ${e.message}")
                    } finally {
                        serverSocket.close()
                    }
                }
            } catch (e: Exception) {
                println("Port $port is already in use, skipping...")
            }
        }
    }

    fun startNewChatSession() {
        val availablePort = findAvailablePort()
        if (availablePort != null) {
            chatRepository.createSession(availablePort)
            println("Chat started on new port $availablePort")

            ServerSocket(availablePort).use { serverSocket ->
                while (true) {
                    val clientSocket = serverSocket.accept()
                    handleClient(clientSocket, availablePort)
                }
            }
        } else {
            println("No available ports found.")
        }
    }

    private fun findAvailablePort(): Int? {
        val usedPorts = chatRepository.getActiveSessions().toSet()
        var port = 10000

        while (port < 65535) {
            if (!usedPorts.contains(port)) {
                return port
            }
            port++
        }
        return null
    }

    private fun handleClient(clientSocket: Socket, port: Int) {
        thread {
            val writer = OutputStreamWriter(clientSocket.getOutputStream())
            synchronized(clients) {
                clients.add(writer)
            }

            clientSocket.getInputStream().bufferedReader().use { reader ->
                val username = reader.readLine()
                println("$username has joined the chat.")
                val chat = chatRepository.getChatByPort(port)

                val chatHistory = chatRepository.getMessagesForSession(chat.id)
                for (message in chatHistory) {
                    writer.write(getColoredMessage("[${message.timestamp.format(formatter)}] ${message.username}: ${message.content}\n", message.username))
                    writer.flush()
                }

                while (true) {
                    val message = reader.readLine() ?: break
                    val timestamp = LocalDateTime.now()
                    val chatId = chat.id
                    val chatMessage = chatRepository.addMessage(username, message, chatId, timestamp)

                    println(getColoredMessage("[${chatMessage.timestamp.format(formatter)}] Received: ${chatMessage.content} from ${chatMessage.username}", chatMessage.username))

                    broadcastMessage(chatMessage)
                }
            }

            synchronized(clients) {
                clients.remove(writer)
            }
        }
    }

    private fun broadcastMessage(chatMessage: ChatMessage) {
        synchronized(clients) {
            for (writer in clients) {
                writer.write(getColoredMessage("[${chatMessage.timestamp.format(formatter)}] ${chatMessage.username}: ${chatMessage.content}\n", chatMessage.username))
                writer.flush()
            }
        }
    }

    private fun getColoredMessage(chatMessage: String?, username: String): String{
        val hash = username.hashCode()
        val colorCode = 31 + (hash % 6)

        val color = "\u001B[${colorCode}m"
        val resetColor = "\u001b[0m"

        return "${color}$chatMessage${resetColor}"
    }
}