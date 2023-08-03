package com.now.naaga.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AdventureResultDto(
    val id: Long,
    val gameId: Long,
    val destination: PlaceDto,
    val resultType: String,
    val score: Int,
    val totalPlayTime: Int,
    val distance: Int,
    val hintUses: Int,
    val tryCount: Int,
    val startTime: String,
    val finishTime: String,
)