package com.now.domain.model

data class Statistics(
    val adventureCount: Int,
    val successCount: Int,
    val failureCount: Int,
    val totalDistance: Int,
    val totalPlayTime: Int,
    val totalHintUses: Int,
)
