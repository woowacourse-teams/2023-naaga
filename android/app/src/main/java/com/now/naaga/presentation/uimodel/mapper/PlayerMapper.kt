package com.now.naaga.presentation.uimodel.mapper

import com.now.domain.model.Player
import com.now.naaga.presentation.uimodel.model.PlayerUiModel

fun Player.toUi(): PlayerUiModel {
    return PlayerUiModel(
        id = id,
        nickname = nickname,
        score = score,
    )
}

fun PlayerUiModel.toDomain(): Player {
    return Player(
        id = id,
        nickname = nickname,
        score = score,
    )
}
