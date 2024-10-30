package com.messenger.chat

import com.messenger.chat.repository.ChatRepository
import com.messenger.chat.repository.DatabaseFactory

fun main() {
    DatabaseFactory.connect()

    val chatRepository = ChatRepository()

    println("Choose an option: ")
    println("Enter 1 to start (start all existing chats)")
    println("Enter 2 to create a new chat")
    println("Enter 3 to join (join an existing chat)\n")

    when (readlnOrNull()?.lowercase()) {
        "1" -> {
            Server(chatRepository).startAllActiveSessions()
        }
        "2" -> {
            Server(chatRepository).startNewChatSession()
        }
        "3" -> {
            Client(chatRepository).join()
        }
        else -> println("Invalid option")
    }
}
