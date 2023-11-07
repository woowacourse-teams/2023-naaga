package com.now.naaga.data.remote.dto.post

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenDto(
    @SerialName("refreshToken")
    val refreshToken: String,
)
