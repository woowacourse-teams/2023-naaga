package com.now.naaga.presentation.uimodel.mapper

import com.now.domain.model.Adventure
import com.now.domain.model.RemainingTryCount
import com.now.naaga.presentation.uimodel.model.AdventureUiModel

fun Adventure.toUi(): AdventureUiModel {
    return AdventureUiModel(
        id = id,
        createdAt = createdAt,
        startCoordinate = startCoordinate.toUi(),
        destination = destination.toUi(),
        player = player.toUi(),
        adventureStatus = adventureStatus,
        remainingTryCount = remainingTryCount.toInt(),
        hints = hints.map { it.toUi() },
    )
}

fun AdventureUiModel.toDomain(): Adventure {
    return Adventure(
        id = id,
        createdAt = createdAt,
        startCoordinate = startCoordinate.toDomain(),
        destination = destination.toDomain(),
        player = player.toDomain(),
        adventureStatus = adventureStatus,
        remainingTryCount = RemainingTryCount(remainingTryCount),
        hints = hints.map { it.toDomain() },
    )
}
