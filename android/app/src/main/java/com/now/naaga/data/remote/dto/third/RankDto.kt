package com.now.naaga.data.remote.dto.third

import kotlinx.serialization.Serializable

@Serializable
data class RankDto(
    val player: PlayerDto,
    val percentage: Int,
    val rank: Int,
)
