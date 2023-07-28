package com.now.naaga.presentation.uimodel.mapper

import com.now.domain.model.Adventure
import com.now.naaga.presentation.uimodel.model.AdventureUiModel

fun Adventure.toUi(): AdventureUiModel {
    return AdventureUiModel(
        id = id,
        destination = destination.toUi(),
        adventureStatus = adventureStatus,
    )
}

fun AdventureUiModel.toDomain(): Adventure {
    return Adventure(
        id = id,
        destination = destination.toDomain(),
        adventureStatus = adventureStatus,
    )
}
