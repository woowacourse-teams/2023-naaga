package com.now.naaga.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class RankDto(
    val player: PlayerDto,
    val percentage: Int,
    val rank: Int,
)
