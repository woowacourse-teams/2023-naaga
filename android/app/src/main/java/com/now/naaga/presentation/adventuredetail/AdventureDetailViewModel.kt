package com.now.naaga.presentation.adventuredetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.now.domain.model.letter.OpenLetter
import com.now.domain.model.type.LogType
import com.now.domain.repository.LetterRepository
import com.now.naaga.data.throwable.DataThrowable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AdventureDetailViewModel @Inject constructor(private val letterRepository: LetterRepository) : ViewModel() {
    private val _readLetters = MutableLiveData<List<OpenLetter>>()
    val readLetters: LiveData<List<OpenLetter>> = _readLetters

    private val _writeLetters = MutableLiveData<List<OpenLetter>>()
    val writeLetters: LiveData<List<OpenLetter>> = _writeLetters

    private val _throwable = MutableLiveData<DataThrowable>()
    val throwable: LiveData<DataThrowable> = _throwable

    fun fetchLetterLogs(gameId: Long, logType: LogType) {
        viewModelScope.launch {
            runCatching {
                letterRepository.fetchLetterLogs(gameId, logType)
            }.onSuccess {
                setLetters(logType, it)
            }.onFailure {
                setThrowable(it)
            }
        }
    }

    private fun setLetters(logType: LogType, letters: List<OpenLetter>) {
        when (logType) {
            LogType.READ -> _readLetters.value = letters
            LogType.WRITE -> _writeLetters.value = letters
        }
    }

    private fun setThrowable(throwable: Throwable) {
        when (throwable) {
            is IOException -> { TODO("_throwable.value = DataThrowable.NetworkThrowable") }
        }
    }
}
