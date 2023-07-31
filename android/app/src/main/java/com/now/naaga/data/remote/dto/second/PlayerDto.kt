package com.now.naaga.data.remote.dto.second

import kotlinx.serialization.Serializable

@Serializable
data class PlayerDto(
    val id: Long,
    val nickname: String,
    val totalScore: Int,
)
