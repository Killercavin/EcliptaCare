package com.brightside.backend.controllers.users.admin

import com.brightside.backend.models.users.admin.dto.responses.AdminProfileResponse
import com.brightside.backend.routes.users.admin.AdminSession
import com.brightside.backend.services.users.admin.AdminService
import io.ktor.server.application.ApplicationCall

/**
 * Main Admin Controller - delegates to specialized sub-controllers
 * Acts as a facade for all admin-related operations
 */
object AdminController {

    // --- Authentication operations ---

    suspend fun login(call: ApplicationCall) =
        AdminAuthController.adminLogin(call)

    suspend fun refreshToken(call: ApplicationCall) =
        AdminAuthController.refreshToken(call)

    // --- Profile operations ---

    suspend fun getAdminProfile(session: AdminSession): AdminProfileResponse {
        return AdminService.getAdminProfile(session.adminId)
    }

    // In future: logout, password reset, etc.
}
