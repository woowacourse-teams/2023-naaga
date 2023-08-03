package com.now.naaga.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PlayerDto(
    val id: Long,
    val nickname: String,
    val totalScore: Int,
)
