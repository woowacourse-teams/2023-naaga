package com.now.domain.model

import java.time.LocalDateTime
import java.time.LocalTime

data class AdventureResult(
    val id: Long,
    val gameId: Long,
    val place: Place,
    val resultType: AdventureResultType,
    val addedScore: Int,
    val playTime: LocalTime,
    val distance: Int,
    val hintUses: Int,
    val tryCount: Int,
    val raisedRank: Int,
    val beginTime: LocalDateTime,
    val endTimeTime: LocalDateTime,
)
