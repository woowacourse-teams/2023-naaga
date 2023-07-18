package com.now.naaga.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DestinationDto(
    val id: Long,
    @SerialName("position")
    val coordinate: CoordinateDto,
    val imageUrl: String,
)
