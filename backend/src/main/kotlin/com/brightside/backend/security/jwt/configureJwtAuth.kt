package com.brightside.backend.security.jwt

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.brightside.backend.configs.connection.EnvConfig
import com.brightside.backend.utils.emum.AdminRole
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import mu.KotlinLogging

private val logger = KotlinLogging.logger {}

fun Application.configureJwtAuth() {
    val secret = EnvConfig.getEnv("JWT_SECRET", "") ?: error("JWT_SECRET is missing")
    val issuer = EnvConfig.getEnv("JWT_ISSUER", "") ?: error("JWT_ISSUER is missing")
    val realm = EnvConfig.getEnv("JWT_REALM", "Access to admin APIs")

    val algorithm = Algorithm.HMAC256(secret)

    install(Authentication) {
        jwt("admin-auth") {
            if (realm != null) {
                this.realm = realm
            }

            verifier(
                JWT.require(algorithm)
                    .withIssuer(issuer)
                    .build()
            )

            validate { credential ->
                val email = credential.payload.getClaim("email").asString()
                val roleString = credential.payload.getClaim("role").asString()
                val id = credential.payload.getClaim("id").asInt()

                val role = try {
                    AdminRole.valueOf(roleString)
                } catch (e: Exception) {
                    logger.warn { "Invalid role in token: $roleString" }
                    null
                }

                if (!email.isNullOrBlank() && id != null && role != null) {
                    logger.debug { "Authenticated admin: $email with role=$role" }
                    JWTPrincipal(credential.payload)
                } else {
                    logger.warn { "Invalid token claims — email: $email, id: $id, role: $roleString" }
                    null
                }
            }

            challenge { _, _ ->
                call.respond(
                    HttpStatusCode.Unauthorized,
                    mapOf("error" to "Token is either invalid or has expired, please login again")
                )
            }
        }
    }
}
