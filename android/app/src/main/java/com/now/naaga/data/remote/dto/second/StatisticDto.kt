package com.now.naaga.data.remote.dto.second

data class StatisticDto(
    val gameCount: Int,
    val successGameCount: Int,
    val failGameCount: Int,
    val totalDistance: Int,
    val totalPlayTime: String,
    val totalUsedHintCount: Int,
)
