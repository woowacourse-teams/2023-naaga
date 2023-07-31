package com.now.naaga.data.remote.dto.second

import com.now.naaga.data.remote.dto.CoordinateDto
import kotlinx.serialization.Serializable

@Serializable
data class FinishGameDto(
    val endType: String,
    val coordinate: CoordinateDto,
)
