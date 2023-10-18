package com.now.naaga.presentation.uimodel.model

data class LetterUiModel(
    val nickname: String,
    val registerDate: String,
    val message: String,
) {
    companion object {
        private const val DEFAULT_MESSAGE = "쪽지가 없습니다."
        val DEFAULT_OPEN_LETTER = LetterUiModel("", "", DEFAULT_MESSAGE)
    }
}
