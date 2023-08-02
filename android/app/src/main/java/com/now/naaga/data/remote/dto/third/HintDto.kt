package com.now.naaga.data.remote.dto.third

import com.now.naaga.data.remote.dto.CoordinateDto
import kotlinx.serialization.Serializable

@Serializable
data class HintDto(
    val id: Long,
    val coordinate: CoordinateDto,
    val direction: String,
)
