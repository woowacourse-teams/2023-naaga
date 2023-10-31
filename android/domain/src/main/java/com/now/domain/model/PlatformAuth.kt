package com.now.domain.model

import com.now.domain.model.type.AuthPlatformType

data class PlatformAuth(
    val token: String,
    val type: AuthPlatformType,
)
