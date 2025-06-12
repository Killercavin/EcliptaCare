package com.brightside.backend.scripts

import at.favre.lib.crypto.bcrypt.BCrypt
import com.brightside.backend.configs.connection.DatabaseFactory
import com.brightside.backend.configs.connection.EnvConfig
import com.brightside.backend.utils.emum.AdminRole
import com.brightside.backend.models.users.admin.tables.AdminTable
import mu.KotlinLogging
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

private val logger = KotlinLogging.logger {}

fun main() {
    runCatching {
        // Initialize DB
        DatabaseFactory.init()

        // Read environment variables
        val firstName = EnvConfig.getEnv("ADMIN_FIRST_NAME", "") ?: error("ADMIN_FIRST_NAME is missing")
        val lastName = EnvConfig.getEnv("ADMIN_LAST_NAME", "") ?: error("ADMIN_LAST_NAME is missing")
        val email = EnvConfig.getEnv("ADMIN_EMAIL", "") ?: error("ADMIN_EMAIL is missing")
        val rawPassword = EnvConfig.getEnv("ADMIN_PASSWORD", "") ?: error("ADMIN_PASSWORD is missing")

        require(firstName.isNotBlank()) { "First name cannot be blank" }
        require(lastName.isNotBlank()) { "Last name cannot be blank" }
        require(email.isNotBlank()) { "Admin email cannot be blank" }
        require(rawPassword.isNotBlank()) { "Admin password cannot be blank" }

        transaction {
            val adminExists = AdminTable.select { AdminTable.email eq email }.singleOrNull()

            if (adminExists != null) {
                logger.warn { "Admin with email '$email' already exists." }
                return@transaction
            }

            val hashedPassword = BCrypt.withDefaults()
                .hashToString(12, rawPassword.toCharArray())

            AdminTable.insert {
                it[AdminTable.firstName] = firstName
                it[AdminTable.lastName] = lastName
                it[AdminTable.email] = email
                it[AdminTable.passwordHash] = hashedPassword
                it[AdminTable.role] = AdminRole.SUPER_ADMIN
            }

            logger.info { "Super admin '$email' created successfully." }
        }
    }.onFailure {
        logger.error(it) { "Admin seeding failed: ${it.message}" }
    }
}
