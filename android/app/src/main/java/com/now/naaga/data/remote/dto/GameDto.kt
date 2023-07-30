package com.now.naaga.data.remote.dto

import kotlinx.serialization.Serializable

// AdventureDto로 네이밍 수정해야됨. 일단 현재 AdventureDto를 지울 수 없어 이렇게 두었음
@Serializable
data class GameDto(
    val id: Long,
    val createdAt: String,
    val startCoordinate: CoordinateDto,
    val destination: PlaceDto,
    val player: PlayerDto,
    val tryCount: Int,
    val gameStatus: String,
    val hints: List<HintDto>,
)
