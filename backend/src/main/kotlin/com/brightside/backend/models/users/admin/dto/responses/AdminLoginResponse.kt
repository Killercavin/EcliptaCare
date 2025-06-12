package com.brightside.backend.models.users.admin.dto.responses

import kotlinx.serialization.Serializable

@Serializable
data class AdminLoginResponse(
    val id: Int,
    val email: String,
    val token: String,
    val refreshToken: String
)
