package com.now.naaga.presentation.uimodel.mapper

import com.now.domain.model.Coordinate
import com.now.naaga.presentation.uimodel.model.CoordinateUiModel

fun CoordinateUiModel.toDomain(): Coordinate {
    return Coordinate(
        latitude = latitude,
        longitude = longitude,
    )
}

fun Coordinate.toUi(): CoordinateUiModel {
    return CoordinateUiModel(
        latitude = latitude,
        longitude = longitude,
    )
}
