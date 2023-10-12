package com.now.naaga.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClosedLetterDto(
    @SerialName("id")
    val id: Long,
    @SerialName("coordinate")
    val coordinateDto: CoordinateDto,
)
