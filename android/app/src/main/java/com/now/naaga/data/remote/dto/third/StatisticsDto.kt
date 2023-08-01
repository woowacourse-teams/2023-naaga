package com.now.naaga.data.remote.dto.third

import kotlinx.serialization.Serializable

@Serializable
data class StatisticsDto(
    val gameCount: Int,
    val successGameCount: Int,
    val failGameCount: Int,
    val totalDistance: Int,
    val totalPlayTime: String,
    val totalUsedHintCount: Int,
)
