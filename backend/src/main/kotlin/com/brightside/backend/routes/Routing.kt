package com.brightside.backend.routes

import com.brightside.backend.controllers.users.admin.AdminAuthController
import com.brightside.backend.controllers.users.admin.AdminController
import com.brightside.backend.services.users.admin.AdminService
import com.brightside.backend.services.users.admin.auth.AdminAuthService
import com.brightside.backend.routes.users.admin.adminRoutes
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    // Initialize services and controllers
    val adminService = AdminService()
    val adminAuthService = AdminAuthService(adminService)
    val adminAuthController = AdminAuthController(adminAuthService)
    val adminController = AdminController(
        adminService = adminService,
        adminAuthService = adminAuthService,
        adminAuthController = adminAuthController
    )

    routing {
        get("/") {
            call.respondText("Ktor Framework")
        }

        // admin routes with class-based controller
        adminRoutes(adminController)
    }
}
