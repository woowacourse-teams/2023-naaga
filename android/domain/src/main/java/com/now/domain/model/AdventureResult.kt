package com.now.domain.model

import java.time.LocalDateTime

data class AdventureResult(
    val id: Long,
    val gameId: Long,
    val destination: Place,
    val resultType: AdventureResultType,
    val score: Int,
    val playTime: Int,
    val distance: Int,
    val hintUses: Int,
    val tryCount: Int,
    val beginTime: LocalDateTime,
    val endTime: LocalDateTime,
)
