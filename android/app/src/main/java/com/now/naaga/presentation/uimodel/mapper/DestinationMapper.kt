package com.now.naaga.presentation.uimodel.mapper

import com.now.domain.model.Destination
import com.now.naaga.presentation.uimodel.model.DestinationUiModel

fun Destination.toUi(): DestinationUiModel {
    return DestinationUiModel(
        id = id,
        coordinate = coordinate.toUi(),
        image = image,
    )
}

fun DestinationUiModel.toDomain(): Destination {
    return Destination(
        id = id,
        coordinate = coordinate.toDomain(),
        image = image,
    )
}
