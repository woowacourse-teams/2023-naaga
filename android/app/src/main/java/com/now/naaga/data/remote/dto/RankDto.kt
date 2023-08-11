package com.now.naaga.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RankDto(
    @SerialName("player")
    val player: PlayerDto,
    @SerialName("percentage")
    val percentage: Int,
    @SerialName("rank")
    val rank: Int,
)
