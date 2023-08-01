package com.now.domain.model

import java.time.LocalTime

data class Statistics(
    val adventureCount: Int,
    val successCount: Int,
    val failureCount: Int,
    val totalDistance: Int,
    val totalPlayTime: LocalTime,
    val totalHintUses: Int,
)