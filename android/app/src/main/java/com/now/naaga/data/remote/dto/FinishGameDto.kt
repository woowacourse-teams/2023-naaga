package com.now.naaga.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class FinishGameDto(
    val endType: String,
    val currentCoordinate: CoordinateDto,
)
