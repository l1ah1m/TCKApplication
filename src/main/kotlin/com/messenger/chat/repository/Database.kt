package com.messenger.chat.repository

import io.github.cdimascio.dotenv.dotenv
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import com.messenger.chat.models.ChatMessages
import com.messenger.chat.models.ChatSessions
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {
    private val dotenv = dotenv()

    fun connect() {
        Database.connect(
            url = dotenv["DB_URL"] ?: throw Exception("DB_URL is not specified"),
            driver = dotenv["DB_DRIVER"] ?: throw Exception("DB_DRIVER is not specified"),
            user = dotenv["DB_USER"] ?: throw Exception("DB_USER is not specified"),
            password = dotenv["DB_PASSWORD"] ?: throw Exception("DB_PASSWORD is not specified")
        )
    }

    fun applyMigrations() {
        transaction {
            SchemaUtils.create(ChatSessions, ChatMessages)
        }
    }

    fun <T> dbQuery(block: () -> T): T = transaction { block() }
}
