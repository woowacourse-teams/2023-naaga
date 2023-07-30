package com.now.domain.model

import java.time.LocalDateTime

data class Game(
    val id: Long,
    val createdAt: LocalDateTime,
    val startCoordinate: Coordinate,
    val destination: Place,
    val player: Player,
    val gameStatus: AdventureStatus,
    val restTryCount: RestTryCount,
    val hints: List<Hint>,
)
