package com.brightside.backend.utils.emum

import kotlinx.serialization.Serializable

@Serializable
// enum class to hold the roles
enum class AdminRole {
    SUPER_ADMIN, // can manage admins
    ADMIN, // can manage products, orders and staff
    STAFF; // view only, limited privileges

    // Permissive roles
    fun canViewAdmin(targetId: Int, requesterId: Int): Boolean {
        return when (this) {
            SUPER_ADMIN -> true
            ADMIN -> requesterId == targetId
            STAFF -> false
        }
    }

    fun canEditAdmin(targetId: Int, requesterId: Int): Boolean {
        return when (this) {
            SUPER_ADMIN -> true
            else -> false
        }
    }

    fun canDeleteAdmin(targetId: Int, requesterId: Int): Boolean {
        return this == SUPER_ADMIN && requesterId != targetId
    }

    // More permissions as needed
}