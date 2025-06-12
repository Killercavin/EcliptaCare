package com.brightside.backend.extensions.users.admin

import com.brightside.backend.routes.users.admin.AdminSession
import com.brightside.backend.utils.emum.AdminRole
import io.ktor.server.auth.jwt.*

fun JWTPrincipal.toAdminSession(): AdminSession {
    val email = this.payload.getClaim("email").asString()
    val id = this.payload.getClaim("id").asInt()
    val role = AdminRole.valueOf(this.payload.getClaim("role").asString())

    return AdminSession(
        adminId = id,
        email = email,
        role = role
    )
}