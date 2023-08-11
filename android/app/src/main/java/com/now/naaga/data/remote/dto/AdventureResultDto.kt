package com.now.naaga.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AdventureResultDto(
    @SerialName("id")
    val id: Long,
    @SerialName("gameId")
    val gameId: Long,
    @SerialName("destination")
    val destination: PlaceDto,
    @SerialName("resultType")
    val resultType: String,
    @SerialName("score")
    val score: Int,
    @SerialName("totalPlayTime")
    val totalPlayTime: Int,
    @SerialName("distance")
    val distance: Int,
    @SerialName("hintUses")
    val hintUses: Int,
    @SerialName("tryCount")
    val tryCount: Int,
    @SerialName("startTime")
    val startTime: String,
    @SerialName("finishTime")
    val finishTime: String,
)
