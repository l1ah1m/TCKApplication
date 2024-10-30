package com.messenger.chat.repository

import com.messenger.chat.models.ChatMessage
import com.messenger.chat.models.ChatSession
import com.messenger.chat.models.ChatMessages
import com.messenger.chat.models.ChatSessions
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.util.UUID

class ChatRepository {

    init {
        DatabaseFactory.connect()
        DatabaseFactory.applyMigrations()
    }

    fun createSession(port: Int): ChatSession {
        val startTime = LocalDateTime.now()
        val id = UUID.randomUUID()
        val res = transaction {
            ChatSessions.insert {
                it[ChatSessions.id] = id
                it[ChatSessions.port] = port
                it[ChatSessions.startTime] = startTime
                it[ChatSessions.isActive] = true
            }
        }
        return ChatSession(id, port, true, startTime)
    }

    fun addMessage(username: String, content: String, chatId: UUID, timestamp: LocalDateTime): ChatMessage {
        val messageId = UUID.randomUUID()
        val res = transaction {
            ChatMessages.insert {
                it[ChatMessages.id] = messageId
                it[ChatMessages.username] = username
                it[ChatMessages.content] = content
                it[ChatMessages.chatId] = chatId
                it[ChatMessages.timestamp] = timestamp
            }
        }
        return ChatMessage(messageId, username, content, chatId, timestamp)
    }

    fun getMessages(chatId: UUID): List<ChatMessage> = transaction {
        ChatMessages.select { ChatMessages.chatId eq chatId }.map {
            ChatMessage(
                id = it[ChatMessages.id],
                username = it[ChatMessages.username],
                content = it[ChatMessages.content],
                chatId = it[ChatMessages.chatId],
                timestamp = it[ChatMessages.timestamp]
            )
        }
    }


    fun getChatByPort(port: Int): ChatSession = transaction {
        ChatSessions.select { ChatSessions.port eq port }
            .mapNotNull {
                ChatSession(
                    id = it[ChatSessions.id],
                    port = it[ChatSessions.port],
                    isActive = it[ChatSessions.isActive],
                    startTime = it[ChatSessions.startTime]
                )
            }
            .singleOrNull() ?: throw NoSuchElementException("Chat session with port $port not found.")
    }

    fun getActiveSessions(): List<Int> {
        return transaction {
            ChatSessions.select { ChatSessions.isActive eq true }
                .map { it[ChatSessions.port] }
        }
    }

    fun getMessagesForSession(chatId: UUID): List<ChatMessage> {
        return transaction {
            ChatMessages.select { ChatMessages.chatId eq chatId }
                .orderBy(ChatMessages.timestamp, SortOrder.ASC)
                .map {
                    ChatMessage(
                        id = it[ChatMessages.id],
                        username = it[ChatMessages.username],
                        content = it[ChatMessages.content],
                        chatId = it[ChatMessages.chatId],
                        timestamp = it[ChatMessages.timestamp]
                    )
                }
        }
    }

}
