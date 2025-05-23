package com.brightside.backend.routes.requests.categories

import kotlinx.serialization.Serializable

@Serializable
data class AddCategoryRequest(
    val name: String,
    val description: String
)