package com.now.naaga.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PositionDto(
    val latitude: Double,
    val longitude: Double,
)
