package com.brightside.backend.security.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.brightside.backend.configs.connection.EnvConfig
import com.brightside.backend.utils.emum.AdminRole
import java.util.*

object JwtProvider {
    private val SECRET: String = EnvConfig.getEnv("JWT_SECRET", "") ?: error("JWT_SECRET is missing")
    private val EXPIRY_MINUTES: Long = EnvConfig.getEnv("JWT_EXPIRY_MINUTES", "45")?.toLongOrNull() ?: 60
    private val ISSUER: String = EnvConfig.getEnv("JWT_ISSUER", "") ?: ""

    private val algorithm = Algorithm.HMAC256(SECRET)

    fun generateToken(adminId: Int, email: String, role: AdminRole): String {
        return JWT.create()
            .withIssuer(ISSUER)
            .withSubject("admin-auth")
            .withClaim("id", adminId)
            .withClaim("email", email)
            .withClaim("role", role.name)
            .withExpiresAt(Date(System.currentTimeMillis() + EXPIRY_MINUTES * 60 * 1000))
            .sign(algorithm)
    }

    fun generateRefreshToken(adminId: Int, email: String, role: AdminRole): String {
        return JWT.create()
            .withIssuer(ISSUER)
            .withSubject("admin-refresh")
            .withClaim("id", adminId)
            .withClaim("email", email)
            .withClaim("role", role.name)
            .withExpiresAt(Date(System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000L))) // 7 days
            .sign(algorithm)
    }
}
