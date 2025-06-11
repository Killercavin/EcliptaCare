package com.brightside.backend.services.users.admin.auth

import at.favre.lib.crypto.bcrypt.BCrypt
import com.brightside.backend.models.users.admin.dto.requests.AdminLoginRequest
import com.brightside.backend.models.users.admin.dto.responses.AdminLoginResponse
import com.brightside.backend.security.jwt.JwtProvider
import com.brightside.backend.services.users.admin.AdminService
import com.brightside.backend.validators.users.admin.AdminLoginValidator
import com.brightside.backend.validators.users.admin.ValidationResult
import com.brightside.backend.infrastructure.redis.RedisClientProvider
import com.auth0.jwt.JWT
import kotlinx.coroutines.future.await

object AdminAuthService {

    suspend fun adminLogin(request: AdminLoginRequest): Result<AdminLoginResponse> {
        when (val validation = AdminLoginValidator.validate(request)) {
            is ValidationResult.Error -> return Result.failure(IllegalArgumentException(validation.message))
            is ValidationResult.Success -> Unit
        }

        val admin = AdminService.getAdminByEmail(request.email)
            ?: return Result.failure(NoSuchElementException("Admin with email ${request.email} not found"))

        val isPasswordCorrect = BCrypt.verifyer().verify(
            request.password.toCharArray(),
            admin.passwordHash
        ).verified

        if (!isPasswordCorrect) {
            return Result.failure(IllegalArgumentException("Incorrect password"))
        }

        val accessToken = JwtProvider.generateToken(admin.email)
        val refreshToken = JwtProvider.generateToken(admin.email)

        RedisClientProvider.asyncCommands.setex("refresh:${admin.email}", 7 * 24 * 60 * 60L, refreshToken).await()

        return Result.success(
            AdminLoginResponse(
                email = admin.email,
                token = accessToken,
                refreshToken = refreshToken
            )
        )
    }

    suspend fun refreshToken(refreshToken: String): Result<AdminLoginResponse> {
        return try {
            val decoded = JWT.decode(refreshToken)
            val email = decoded.getClaim("email").asString()

            val stored = RedisClientProvider.asyncCommands.get("refresh:$email").await()
            if (stored != refreshToken) {
                return Result.failure(SecurityException("Refresh token mismatch"))
            }

            val newAccessToken = JwtProvider.generateToken(email)

            Result.success(
                AdminLoginResponse(
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
