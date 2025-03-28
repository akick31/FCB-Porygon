package com.fcb.porygon.models.website

import com.fcb.porygon.domain.User

data class LoginResponse(
    val token: String,
    val userId: Long,
    val role: User.Role,
)
