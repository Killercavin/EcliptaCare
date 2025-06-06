package com.brightside.backend.models.users.admin.dto.responses

import kotlinx.serialization.Serializable

@Serializable
data class AdminErrorResponse(
    val error: String,
    val code: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

// Create specific error codes as constants
object ErrorCodes {
    const val VALIDATION_ERROR = "VALIDATION_ERROR"
    const val INVALID_REQUEST = "INVALID_REQUEST"
    const val INVALID_CREDENTIALS = "INVALID_CREDENTIALS"
    const val ACCOUNT_DISABLED = "ACCOUNT_DISABLED"
    const val INTERNAL_ERROR = "INTERNAL_ERROR"
    const val SERVICE_ERROR = "SERVICE_ERROR"
    const val UNAUTHORIZED = "UNAUTHORIZED"
}