package com.brightside.backend.controllers.users.admin

import com.brightside.backend.models.users.admin.dto.requests.AdminLoginRequest
import com.brightside.backend.models.users.admin.dto.requests.RefreshTokenRequest
import com.brightside.backend.models.users.admin.dto.responses.AdminErrorResponse
import com.brightside.backend.models.users.admin.dto.responses.ErrorCodes
import com.brightside.backend.services.users.admin.auth.AdminAuthService
import com.brightside.backend.validators.users.admin.AdminLoginValidator
import com.brightside.backend.validators.users.admin.ValidationResult
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*

/**
 * Specialized controller for admin authentication operations
 * Handles: login, logout, token refresh, password reset
 */
object AdminAuthController {

    suspend fun adminLogin(call: ApplicationCall) {
        try {
            val request = parseAndValidateLoginRequest(call)
            val result = AdminAuthService.adminLogin(request)

            result.onSuccess { response ->
                call.respond(HttpStatusCode.OK, response)
            }.onFailure { error ->
                call.application.log.warn("Login failed for email=${request.email}: ${error.message}")
                handleAuthError(call, error)
            }

        } catch (e: BadRequestException) {
            call.respondError(HttpStatusCode.BadRequest, "Invalid request format", ErrorCodes.INVALID_REQUEST)
        } catch (e: ValidationException) {
            call.respondError(HttpStatusCode.BadRequest, e.message ?: "Validation failed", ErrorCodes.VALIDATION_ERROR)
        } catch (e: Exception) {
            call.application.log.error("Unexpected error in admin login", e)
            call.respondError(HttpStatusCode.InternalServerError, "An unexpected error occurred", ErrorCodes.INTERNAL_ERROR)
        }
    }

    suspend fun refreshToken(call: ApplicationCall) {
        try {
            val refreshToken = call.receive<RefreshTokenRequest>().refreshToken
            val result = AdminAuthService.refreshToken(refreshToken)

            result.fold(
                onSuccess = { response ->
                    call.respond(HttpStatusCode.OK, response)
                },
                onFailure = { ex ->
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        AdminErrorResponse(
                            error = ex.message ?: "Invalid refresh token",
                            code = ErrorCodes.UNAUTHORIZED
                        )
                    )
                }
            )
        } catch (e: Exception) {
            call.application.log.error("Unexpected error in token refresh", e)
            call.respond(
                HttpStatusCode.BadRequest,
                AdminErrorResponse("Invalid refresh token format", ErrorCodes.INVALID_REQUEST)
            )
        }
    }

    private suspend fun parseAndValidateLoginRequest(call: ApplicationCall): AdminLoginRequest {
        val request = try {
            call.receive<AdminLoginRequest>()
        } catch (e: Exception) {
            throw BadRequestException("Invalid JSON format")
        }

        when (val validationResult = AdminLoginValidator.validate(request)) {
            is ValidationResult.Success -> return request
            is ValidationResult.Error -> throw ValidationException(validationResult.message)
        }
    }

    private suspend fun handleAuthError(call: ApplicationCall, error: Throwable) {
        when (error) {
            is SecurityException -> {
                call.respondError(HttpStatusCode.Unauthorized, "Invalid credentials", ErrorCodes.INVALID_CREDENTIALS)
            }
            is IllegalStateException -> {
                call.respondError(HttpStatusCode.Forbidden, "Account is disabled or locked", ErrorCodes.ACCOUNT_DISABLED)
            }
            else -> {
                call.application.log.error("Auth service error", error)
                call.respondError(HttpStatusCode.InternalServerError, "Authentication service unavailable", ErrorCodes.SERVICE_ERROR)
            }
        }
    }
}

// Extension function for consistent error responses
suspend fun ApplicationCall.respondError(
    status: HttpStatusCode,
    message: String,
    code: String
) {
    respond(status, AdminErrorResponse(error = message, code = code))
}

// Validation exception used internally
class ValidationException(message: String) : Exception(message)
