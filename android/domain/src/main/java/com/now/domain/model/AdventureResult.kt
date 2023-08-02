package com.now.domain.model

import java.time.LocalDateTime
import java.time.LocalTime

data class AdventureResult(
    val id: Long,
    val gameId: Long,
    val destination: Place,
    val resultType: AdventureResultType,
    val score: Int,
    val playTime: LocalTime,
    val distance: Int,
    val hintUses: Int,
    val tryCount: Int,
    val beginTime: LocalDateTime,
    val endTime: LocalDateTime,
)
