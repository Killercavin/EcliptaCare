package com.brightside.backend.services.users.admin.auth

import at.favre.lib.crypto.bcrypt.BCrypt
import com.auth0.jwt.JWT
import com.brightside.backend.infrastructure.redis.RedisClientProvider
import com.brightside.backend.models.users.admin.dto.requests.AdminLoginRequest
import com.brightside.backend.models.users.admin.dto.responses.AdminLoginResponse
import com.brightside.backend.security.jwt.JwtProvider
import com.brightside.backend.services.users.admin.AdminService
import com.brightside.backend.validators.users.admin.AdminLoginValidator
import com.brightside.backend.validators.users.admin.ValidationResult
import kotlinx.coroutines.future.await
import org.slf4j.LoggerFactory

class AdminAuthService(
    private val adminService: AdminService
) {
    private val logger = LoggerFactory.getLogger(AdminAuthService::class.java)

    suspend fun adminLogin(request: AdminLoginRequest): Result<AdminLoginResponse> {
        // Validate request fields (basic format checks)
        when (val validation = AdminLoginValidator.validate(request)) {
            is ValidationResult.Error -> {
                logger.warn("Validation failed for email='${request.email}': ${validation.message}")
                return Result.failure(SecurityException("Invalid email or password"))
            }
            is ValidationResult.Success -> Unit
        }

        // Unified login failure response to avoid credential enumeration
        fun failedLogin(reason: String): Result<AdminLoginResponse> {
            logger.warn("Login failed for email='${request.email}': $reason")
            return Result.failure(SecurityException("Invalid email or password"))
        }

        // Try to fetch the admin by email
        val admin = adminService.getAdminByEmail(request.email)
            ?: return failedLogin("Email not found")

        // Check the password securely
        val passwordMatches = BCrypt.verifyer().verify(
            request.password.toCharArray(),
            admin.passwordHash
        ).verified

        if (!passwordMatches) return failedLogin("Incorrect password")

        return try {
            // Generate access and refresh tokens
            val accessToken = JwtProvider.generateToken(admin.id, admin.email, admin.role)
            val refreshToken = JwtProvider.generateRefreshToken(admin.id, admin.email, admin.role)

            // Save the refresh token in Redis with 1-day expiration
            RedisClientProvider.asyncCommands.setex(
                "refresh:${admin.email}",
                1 * 24 * 60 * 60L,
                refreshToken
            ).await()

            Result.success(
                AdminLoginResponse(
                    id = admin.id,
                    email = admin.email,
                    token = accessToken,
                    refreshToken = refreshToken
                )
            )
        } catch (e: Exception) {
            logger.error("Login failed during token generation or Redis storage", e)
            Result.failure(IllegalStateException("Authentication service unavailable"))
        }
    }

    suspend fun refreshToken(refreshToken: String): Result<AdminLoginResponse> {
        return try {
            val decoded = JWT.decode(refreshToken)
            val email = decoded.getClaim("email").asString()

            val stored = RedisClientProvider.asyncCommands.get("refresh:$email").await()
            if (stored != refreshToken) {
                return Result.failure(SecurityException("Refresh token mismatch"))
            }

            val admin = adminService.getAdminByEmail(email)
                ?: return Result.failure(NoSuchElementException("Admin not found"))

            val newAccessToken = JwtProvider.generateToken(admin.id, admin.email, admin.role)

            Result.success(
                AdminLoginResponse(
                    id = admin.id,
                    email = email,
                    token = newAccessToken,
                    refreshToken = refreshToken
                )
            )
        } catch (e: Exception) {
            Result.failure(SecurityException("Invalid or expired refresh token"))
        }
    }
}