package com.now.naaga.data.mapper

import com.now.domain.model.Adventure
import com.now.domain.model.AdventureStatus
import com.now.domain.model.RemainingTryCount
import com.now.naaga.data.remote.dto.AdventureDto
import com.now.naaga.data.remote.dto.EndedAdventureDto
import java.time.LocalDateTime

fun AdventureDto.toDomain(): Adventure {
    return Adventure(
        id = id,
        createdAt = LocalDateTime.parse(startTime),
        startCoordinate = startCoordinate.toDomain(),
        destination = destination.toDomain(),
        player = player.toDomain(),
        adventureStatus = AdventureStatus.getStatus(gameStatus),
        remainingTryCount = RemainingTryCount(remainingAttempts),
        hints = hints.map { it.toDomain() },
    )
}

fun EndedAdventureDto.toDomain(): AdventureStatus {
    return AdventureStatus.getStatus(adventureStatus)
}
