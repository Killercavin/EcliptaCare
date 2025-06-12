package com.brightside.backend.models.users.admin.dto

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
data class AdminDTO(
    @Contextual val id: Int,
    val fullName: String,
    val email: String,
    @Contextual val createdAt: String,
    @Contextual val updatedAt: String,
)