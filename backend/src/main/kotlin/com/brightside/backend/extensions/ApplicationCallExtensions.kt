package com.brightside.backend.extensions

import com.brightside.backend.models.users.admin.dto.responses.AdminErrorResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*

suspend fun ApplicationCall.respondError(
    status: HttpStatusCode,
    message: String,
    code: String
) {
    respond(status, AdminErrorResponse(error = message, code = code))
}