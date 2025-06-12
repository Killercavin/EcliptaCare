package com.brightside.backend.utils.emum

import kotlinx.serialization.Serializable

@Serializable
// enum class to hold the roles
enum class AdminRole {
    SUPER_ADMIN, // can manage admins
    ADMIN, // can manage products, orders and staff
    STAFF // view only, limited privileges
}