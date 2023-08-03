package com.now.naaga.presentation.uimodel.mapper

import com.now.domain.model.Hint
import com.now.naaga.presentation.uimodel.model.HintUiModel

fun Hint.toUi(): HintUiModel {
    return HintUiModel(
        id = id,
        coordinate = coordinate.toUi(),
        direction = direction,
    )
}

fun HintUiModel.toDomain(): Hint {
    return Hint(
        id = id,
        coordinate = coordinate.toDomain(),
        direction = direction,
    )
}
