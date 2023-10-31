package com.now.naaga.presentation.uimodel.mapper

import com.now.domain.model.letter.Letter
import com.now.naaga.presentation.uimodel.model.LetterUiModel

fun Letter.toUiModel(): LetterUiModel {
    return LetterUiModel(
        nickname = player.nickname,
        registerDate = registerDate,
        message = message,
    )
}
