package com.now.naaga.presentation.adventuredetail

import com.now.domain.model.AdventureResult
import com.now.naaga.presentation.uimodel.model.LetterUiModel

sealed interface AdventureDetailUiState {
    object Loading : AdventureDetailUiState

    data class Success(
        val readLetters: List<LetterUiModel>,
        val writeLetters: List<LetterUiModel>,
        val adventureResult: AdventureResult,
    ) : AdventureDetailUiState

    object Error : AdventureDetailUiState
}
