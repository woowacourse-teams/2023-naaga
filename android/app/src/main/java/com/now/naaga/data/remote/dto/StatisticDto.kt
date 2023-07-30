package com.now.naaga.data.remote.dto

data class StatisticDto(
    val gameCount: Int,
    val successGameCount: Int,
    val failGameCount: Int,
    val totalDistance: Int,
    val totalPlayTime: Int,
    val usedHintCount: Int,
)
