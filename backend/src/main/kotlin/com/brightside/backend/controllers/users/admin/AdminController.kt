package com.brightside.backend.controllers.users.admin

import com.brightside.backend.models.users.admin.dto.responses.AdminProfileResponse
import com.brightside.backend.routes.users.admin.AdminSession
import com.brightside.backend.services.users.admin.AdminService
import com.brightside.backend.services.users.admin.auth.AdminAuthService
import io.ktor.server.application.ApplicationCall

/**
 * Main Admin Controller - delegates to specialized sub-controllers
 * Acts as a facade for all admin-related operations
 */
class AdminController(
    private val adminService: AdminService,
    private val adminAuthService: AdminAuthService,
    private val adminAuthController: AdminAuthController
) {

    // --- Authentication operations ---

    suspend fun login(call: ApplicationCall) =
        adminAuthController.adminLogin(call)

    suspend fun refreshToken(call: ApplicationCall) =
        adminAuthController.refreshToken(call)

    // --- Profile operations ---

    suspend fun getAdminProfile(session: AdminSession): AdminProfileResponse {
        return adminService.getAdminProfile(session.adminId)
    }

    // Future: logout, password reset, etc.
}
