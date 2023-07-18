package com.now.naaga.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class DestinationDto(
    val id: Long,
    val position: PositionDto,
    val imageUrl: String,
)
