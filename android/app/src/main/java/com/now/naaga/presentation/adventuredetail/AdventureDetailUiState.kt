package com.now.naaga.presentation.adventuredetail

import com.now.naaga.presentation.uimodel.model.OpenLetterUiModel

sealed interface AdventureDetailUiState {
    object Loading : AdventureDetailUiState

    data class Success(
        val readLetters: List<OpenLetterUiModel>,
        val writeLetters: List<OpenLetterUiModel>,
    ) : AdventureDetailUiState

    object Error : AdventureDetailUiState
}
