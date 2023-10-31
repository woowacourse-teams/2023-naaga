package com.now.naaga.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatisticsDto(
    @SerialName("gameCount")
    val gameCount: Int,
    @SerialName("successGameCount")
    val successGameCount: Int,
    @SerialName("failGameCount")
    val failGameCount: Int,
    @SerialName("totalDistance")
    val totalDistance: Int,
    @SerialName("totalPlayTime")
    val totalPlayTime: Int,
    @SerialName("totalUsedHintCount")
    val totalUsedHintCount: Int,
)
