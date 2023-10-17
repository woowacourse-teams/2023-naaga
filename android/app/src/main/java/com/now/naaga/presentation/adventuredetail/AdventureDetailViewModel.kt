package com.now.naaga.presentation.adventuredetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.now.domain.model.letter.OpenLetter
import com.now.domain.model.type.LogType
import com.now.domain.repository.LetterRepository
import com.now.naaga.data.throwable.DataThrowable
import com.now.naaga.presentation.uimodel.mapper.toUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AdventureDetailViewModel @Inject constructor(
    private val letterRepository: LetterRepository,
) : ViewModel() {
    private val readLettersFlow = MutableSharedFlow<List<OpenLetter>>()

    private val writeLettersFlow = MutableSharedFlow<List<OpenLetter>>()

    private val _uiState: MutableStateFlow<AdventureDetailUiState> = MutableStateFlow(AdventureDetailUiState.Loading)
    val uiState: StateFlow<AdventureDetailUiState> = _uiState.asStateFlow()

    private val _throwable = MutableLiveData<DataThrowable>()
    val throwable: LiveData<DataThrowable> = _throwable

    init {
        viewModelScope.launch {
            combine(readLettersFlow, writeLettersFlow) { readLetters, writeLetters ->
                AdventureDetailUiState.Success(
                    readLetters = readLetters.map { it.toUiModel() },
                    writeLetters = writeLetters.map { it.toUiModel() },
                )
            }.collectLatest { _uiState.value = it }
        }
    }

    fun fetchReadLetter(gameId: Long) {
        viewModelScope.launch {
            runCatching {
                letterRepository.fetchLetterLogs(gameId, LogType.READ)
            }.onSuccess {
                readLettersFlow.emit(it)
            }
        }
    }

    fun fetchWriteLetter(gameId: Long) {
        viewModelScope.launch {
            runCatching {
                letterRepository.fetchLetterLogs(gameId, LogType.WRITE)
            }.onSuccess {
                writeLettersFlow.emit(it)
            }
        }
    }

    private fun setThrowable(throwable: Throwable) {
        when (throwable) {
            is IOException -> {
                TODO("_throwable.value = DataThrowable.NetworkThrowable")
            }

            is DataThrowable.LetterThrowable -> {
                _throwable.value = throwable
            }
        }
    }
}
