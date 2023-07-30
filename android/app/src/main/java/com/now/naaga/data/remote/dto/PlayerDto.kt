package com.now.naaga.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class PlayerDto(
    val id: Long,
    val nickname: String,
    val totalScore: Int,
)

// 바라는 내용
/*val id: Long,
val name: String,
val score: Int,
val percent: Int,
val rank: Int,*/
