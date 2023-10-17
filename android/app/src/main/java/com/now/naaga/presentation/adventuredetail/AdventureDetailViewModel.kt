package com.now.naaga.presentation.adventuredetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.now.domain.model.AdventureResult
import com.now.domain.model.letter.OpenLetter
import com.now.domain.model.type.LogType
import com.now.domain.repository.AdventureRepository
import com.now.domain.repository.LetterRepository
import com.now.naaga.data.throwable.DataThrowable
import com.now.naaga.presentation.uimodel.mapper.toUiModel
import com.now.naaga.presentation.uimodel.model.OpenLetterUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AdventureDetailViewModel @Inject constructor(
    private val letterRepository: LetterRepository,
    private val adventureRepository: AdventureRepository,
) : ViewModel() {
    private val readLettersFlow = MutableSharedFlow<List<OpenLetter>>()

    private val writeLettersFlow = MutableSharedFlow<List<OpenLetter>>()

    private val adventureFlow = MutableSharedFlow<AdventureResult>()

    private val _uiState: MutableStateFlow<AdventureDetailUiState> = MutableStateFlow(AdventureDetailUiState.Loading)
    val uiState: StateFlow<AdventureDetailUiState> = _uiState.asStateFlow()

    private val _throwableFlow = MutableSharedFlow<Event>()
    val throwableFlow: SharedFlow<Event> = _throwableFlow.asSharedFlow()

    init {
        viewModelScope.launch {
            combine(readLettersFlow, writeLettersFlow, adventureFlow) { readLetters, writeLetters, adventureResult ->
                AdventureDetailUiState.Success(
                    readLetters = getOpenLetterUiModels(readLetters),
                    writeLetters = getOpenLetterUiModels(writeLetters),
                    adventureResult = adventureResult,
                )
            }.collectLatest { _uiState.value = it }
        }
    }

    private fun getOpenLetterUiModels(letters: List<OpenLetter>): List<OpenLetterUiModel> {
        if (letters.isEmpty()) return listOf(OpenLetterUiModel.DEFAULT_OPEN_LETTER)
        return letters.map { it.toUiModel() }
    }

    fun fetchReadLetter(gameId: Long) {
        viewModelScope.launch {
            runCatching {
                letterRepository.fetchLetterLogs(gameId, LogType.READ)
            }.onSuccess {
                readLettersFlow.emit(it)
            }.onFailure {
                setThrowable(it)
            }
        }
    }

    fun fetchWriteLetter(gameId: Long) {
        viewModelScope.launch {
            runCatching {
                letterRepository.fetchLetterLogs(gameId, LogType.WRITE)
            }.onSuccess {
                writeLettersFlow.emit(it)
            }.onFailure {
                setThrowable(it)
            }
        }
    }

    fun fetchAdventureResult(gameId: Long) {
        viewModelScope.launch {
            runCatching {
                adventureRepository.fetchAdventureResult(gameId)
            }.onSuccess {
                adventureFlow.emit(it)
            }.onFailure {
                setThrowable(it)
            }
        }
    }

    private fun setThrowable(throwable: Throwable) {
        when (throwable) {
            is IOException -> throwable(Event.NetworkExceptionEvent(throwable))
            is DataThrowable.LetterThrowable -> throwable(Event.LetterExceptionEvent(throwable))
            is DataThrowable.GameThrowable -> throwable(Event.GameExceptionEvent(throwable))
        }
    }

    private fun throwable(event: Event) {
        viewModelScope.launch {
            _throwableFlow.emit(event)
        }
    }

    sealed class Event {
        data class NetworkExceptionEvent(val throwable: Throwable) : Event()
        data class LetterExceptionEvent(val throwable: Throwable) : Event()
        data class GameExceptionEvent(val throwable: Throwable) : Event()
    }
}
