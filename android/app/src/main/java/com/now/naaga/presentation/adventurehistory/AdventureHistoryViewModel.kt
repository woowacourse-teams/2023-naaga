package com.now.naaga.presentation.adventurehistory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.now.domain.model.AdventureResult
import com.now.domain.model.type.OrderType
import com.now.domain.model.type.SortType
import com.now.domain.repository.AdventureRepository
import com.now.naaga.data.throwable.DataThrowable
import com.now.naaga.data.throwable.DataThrowable.PlayerThrowable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdventureHistoryViewModel @Inject constructor(private val adventureRepository: AdventureRepository) :
    ViewModel() {
    private val _adventureResults = MutableLiveData<List<AdventureResult>>()
    val adventureResults: LiveData<List<AdventureResult>> = _adventureResults

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun fetchHistories() {
        viewModelScope.launch {
            runCatching {
                adventureRepository.fetchMyAdventureResults(SortType.TIME, OrderType.DESCENDING)
            }.onSuccess { results: List<AdventureResult> ->
                _adventureResults.value = results
            }.onFailure {
                setErrorMessage(it as DataThrowable)
            }
        }
    }

    private fun setErrorMessage(throwable: DataThrowable) {
        when (throwable) {
            is PlayerThrowable -> {
                _errorMessage.value = throwable.message
            }

            else -> {}
        }
    }
}
