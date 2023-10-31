package com.now.naaga.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdventureDto(
    @SerialName("id")
    val id: Long,
    @SerialName("startTime")
    val startTime: String,
    @SerialName("gameStatus")
    val gameStatus: String,
    @SerialName("startCoordinate")
    val startCoordinate: CoordinateDto,
    @SerialName("destination")
    val destination: PlaceDto,
    @SerialName("player")
    val player: PlayerDto,
    @SerialName("remainingAttempts")
    val remainingAttempts: Int,
    @SerialName("hints")
    val hints: List<HintDto>,
)
