package com.brightside.backend.routes.users.admin

import com.brightside.backend.controllers.users.admin.AdminController
import com.brightside.backend.extensions.users.admin.toAdminSession
import com.brightside.backend.models.users.admin.dto.responses.AdminErrorResponse
import com.brightside.backend.models.users.admin.dto.responses.ErrorCodes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.adminRoutes(controller: AdminController) {
    route("/api/admin") {

        // Auth routes
        post("/login") { controller.login(call) }
        post("/token/refresh") { controller.refreshToken(call) }

        // Secure routes
        authenticate("admin-auth") {
            get("/me") {
                val principal = call.principal<JWTPrincipal>()
                if (principal == null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        AdminErrorResponse("JWT principal not found", ErrorCodes.UNAUTHORIZED)
                    )
                    return@get
                }

                val adminSession = try {
                    principal.toAdminSession()
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        AdminErrorResponse("Invalid JWT claims: ${e.message}", ErrorCodes.UNAUTHORIZED)
                    )
                    return@get
                }

                try {
                    val profile = controller.getAdminProfile(adminSession)
                    call.respond(HttpStatusCode.OK, profile)
                } catch (e: Exception) {
                    call.application.log.error("Error fetching admin profile", e)
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        AdminErrorResponse("Could not retrieve admin profile", ErrorCodes.SERVICE_ERROR)
                    )
                }
            }
        }
    }
}
