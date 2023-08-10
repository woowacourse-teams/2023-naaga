package com.now.domain.model

data class PlatformAuth(
    val token: String,
    val type: AuthPlatformType,
)
