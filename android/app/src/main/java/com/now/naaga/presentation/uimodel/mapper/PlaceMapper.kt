package com.now.naaga.presentation.uimodel.mapper

import com.now.domain.model.Place
import com.now.naaga.presentation.uimodel.model.PlaceUiModel

fun Place.toUi(): PlaceUiModel {
    return PlaceUiModel(
        id = id,
        name = name,
        coordinate = coordinate.toUi(),
        image = image,
        description = description,
    )
}

fun PlaceUiModel.toDomain(): Place {
    return Place(
        id = id,
        name = name,
        coordinate = coordinate.toDomain(),
        image = image,
        description = description,
    )
}
