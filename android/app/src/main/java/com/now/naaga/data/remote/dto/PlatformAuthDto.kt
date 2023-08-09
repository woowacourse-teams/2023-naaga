package com.now.naaga.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PlatformAuthDto(
    val token: String,
    val type: String,
)
