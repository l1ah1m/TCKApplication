package com.messenger.chat.models

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime
import java.util.*

data class ChatSession(
    val id: UUID,
    val port: Int,
    val isActive: Boolean,
    val startTime: LocalDateTime
)

object ChatSessions : Table() {
    val id = uuid("id").uniqueIndex()
    val port = integer("port").uniqueIndex()
    val startTime = datetime("start_time")
    val isActive = bool("is_active").default(false)

    override val primaryKey = PrimaryKey(id)
}
