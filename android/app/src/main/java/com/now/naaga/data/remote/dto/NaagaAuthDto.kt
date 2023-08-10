package com.now.naaga.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class NaagaAuthDto(
    val accessToken: String,
    val refreshToken: String = "",
)
