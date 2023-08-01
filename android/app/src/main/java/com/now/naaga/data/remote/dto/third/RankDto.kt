package com.now.naaga.data.remote.dto.third

import kotlinx.serialization.Serializable

@Serializable
data class RankDto(
    val player: PlayerDto,
    val percentage: Double,
    val rank: Int,
)
