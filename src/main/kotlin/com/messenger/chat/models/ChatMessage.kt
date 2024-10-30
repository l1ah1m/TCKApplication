package com.messenger.chat.models

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.util.UUID

data class ChatMessage(
    val id: UUID,
    val username: String,
    val content: String,
    val chatId: UUID,
    val timestamp: LocalDateTime,
)

object ChatMessages : Table() {
    val id = uuid("id").uniqueIndex()
    val username = varchar("username", 50)
    val content = text("content")
    val chatId = uuid("chat_id").references(ChatSessions.id, onDelete = ReferenceOption.CASCADE)
    val timestamp = datetime("timestamp")

    override val primaryKey = PrimaryKey(id)
}