package com.now.naaga.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlatformAuthDto(
    @SerialName("token")
    val token: String,
    @SerialName("type")
    val type: String,
)
