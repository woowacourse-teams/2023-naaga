package com.now.naaga.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HintDto(
    @SerialName("id")
    val id: Long,
    @SerialName("coordinate")
    val coordinate: CoordinateDto,
    @SerialName("direction")
    val direction: String,
)
