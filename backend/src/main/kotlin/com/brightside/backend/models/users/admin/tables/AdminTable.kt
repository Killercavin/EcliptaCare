package com.brightside.backend.models.users.admin.tables

import com.brightside.backend.utils.emum.AdminRole
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.CurrentTimestamp
import org.jetbrains.exposed.sql.javatime.timestamp

object AdminTable : IntIdTable("admins") {
    val firstName = varchar("first_name", 50)
    val lastName = varchar("last_name", 50)
    val email = varchar("email", 255).uniqueIndex()
    val passwordHash = text("password_hash")
    val role = enumerationByName("role", 20, AdminRole::class).default(AdminRole.STAFF)
    val createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp())
    val updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp())
}