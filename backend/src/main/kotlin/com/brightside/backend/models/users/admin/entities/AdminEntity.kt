package com.brightside.backend.models.users.admin.entities

import com.brightside.backend.utils.emum.AdminRole
import kotlinx.serialization.Serializable

// internal logic representation
@Serializable
data class AdminEntity(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: AdminRole,
    val passwordHash: String,
    val createdAt: String,
    val updatedAt: String
) {
    val fullName: String
        get() = "$firstName $lastName".trim()
}

