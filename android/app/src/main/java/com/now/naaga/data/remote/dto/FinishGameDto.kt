package com.now.naaga.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FinishGameDto(
    @SerialName("endType")
    val endType: String,
    @SerialName("coordinate")
    val coordinate: CoordinateDto,
)
