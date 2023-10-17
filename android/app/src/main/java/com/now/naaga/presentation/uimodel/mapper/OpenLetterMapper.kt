package com.now.naaga.presentation.uimodel.mapper

import com.now.domain.model.letter.OpenLetter
import com.now.naaga.presentation.uimodel.model.OpenLetterUiModel

fun OpenLetter.toUiModel(): OpenLetterUiModel {
    return OpenLetterUiModel(
        nickname = player.nickname,
        registerDate = registerDate,
        message = message,
    )
}
