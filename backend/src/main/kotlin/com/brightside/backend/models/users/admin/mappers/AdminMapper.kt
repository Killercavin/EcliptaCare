package com.brightside.backend.models.users.admin.mappers

import com.brightside.backend.models.users.admin.dto.AdminDTO
import com.brightside.backend.models.users.admin.dto.responses.AdminProfileResponse
import com.brightside.backend.models.users.admin.entities.AdminEntity

fun AdminEntity.toDTO(): AdminDTO = AdminDTO(
    id = this.id,
    email = this.email,
    createdAt = this.createdAt,
    updatedAt = this.updatedAt,
    fullName = this.fullName
)

fun AdminEntity.toProfileResponse(): AdminProfileResponse {
    return AdminProfileResponse(
        id = this.id,
        email = this.email,
        fullName = this.fullName,
        role = this.role,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

