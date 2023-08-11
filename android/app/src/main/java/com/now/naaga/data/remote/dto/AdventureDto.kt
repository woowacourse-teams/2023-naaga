package com.now.naaga.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// AdventureDto로 네이밍 수정해야됨. 일단 현재 AdventureDto를 지울 수 없어 이렇게 두었음
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
