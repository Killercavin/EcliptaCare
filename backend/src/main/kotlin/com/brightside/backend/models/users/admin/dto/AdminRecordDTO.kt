package com.brightside.backend.models.users.admin.dto

import kotlinx.serialization.Serializable

@Serializable
data class AdminRecordDTO(
    val email: String,
    val firstName: String,
    val lastName: String,
    val passwordHash: String
)