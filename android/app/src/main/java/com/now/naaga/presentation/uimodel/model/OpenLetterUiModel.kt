package com.now.naaga.presentation.uimodel.model

data class OpenLetterUiModel(
    val nickname: String,
    val registerDate: String,
    val message: String,
) {
    companion object {
        private const val DEFAULT_MESSAGE = "쪽지가 없습니다."

        fun getDefault(): OpenLetterUiModel {
            return OpenLetterUiModel("", "", DEFAULT_MESSAGE)
        }
    }
}
