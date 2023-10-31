package com.now.naaga.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LetterDto(
    @SerialName("id")
    val id: Long,
    @SerialName("player")
    val player: PlayerDto,
    @SerialName("coordinate")
    val coordinateDto: CoordinateDto,
    @SerialName("message")
    val message: String,
    @SerialName("registerDate")
    val registerDate: String,
)
