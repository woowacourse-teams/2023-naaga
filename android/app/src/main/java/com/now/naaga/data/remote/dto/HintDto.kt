package com.now.naaga.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class HintDto(
    val id: Long,
    val coordinate: CoordinateDto,
    val direction: String,
)
