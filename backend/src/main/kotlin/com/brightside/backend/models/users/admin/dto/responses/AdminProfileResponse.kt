package com.brightside.backend.models.users.admin.dto.responses

import com.brightside.backend.utils.emum.AdminRole
import kotlinx.serialization.Serializable

@Serializable
data class AdminProfileResponse(
    val id: Int,
    val email: String,
    val fullName: String,
    val role: AdminRole,
    val createdAt: String,
    val updatedAt: String,
)