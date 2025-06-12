package com.brightside.backend.routes.users.admin

import com.brightside.backend.utils.emum.AdminRole
import kotlinx.serialization.Serializable

@Serializable
data class AdminSession(
    val adminId: Int,
    val email: String,
    val role: AdminRole  // Optional enhancement for future role-based features
)
