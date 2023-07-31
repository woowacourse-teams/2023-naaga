package com.now.naaga.data.remote.dto.third

import com.now.naaga.data.remote.dto.CoordinateDto
import kotlinx.serialization.Serializable

@Serializable
data class PlaceDto(
    val id: Long,
    val name: String,
    val coordinate: CoordinateDto,
    val imageUrl: String,
    val description: String,
)
