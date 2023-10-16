package com.now.naaga.presentation.adventureresult

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.now.domain.model.AdventureResult
import com.now.domain.repository.AdventureRepository
import com.now.domain.repository.RankRepository
import com.now.naaga.data.throwable.DataThrowable
import com.now.naaga.data.throwable.DataThrowable.GameThrowable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdventureResultViewModel @Inject constructor(
    private val adventureRepository: AdventureRepository,
    private val rankRepository: RankRepository,
) : ViewModel() {

    private val _adventureResult = MutableLiveData<AdventureResult>()
    val adventureResult: LiveData<AdventureResult> = _adventureResult

    private val _myRank = MutableLiveData<Int>()
    val myRank: LiveData<Int> = _myRank

    private val _throwable = MutableLiveData<DataThrowable>()
    val throwable: LiveData<DataThrowable> = _throwable

    fun fetchGameResult(adventureId: Long) {
        viewModelScope.launch {
            runCatching {
                adventureRepository.fetchAdventureResult(adventureId)
            }.onSuccess { adventureResult ->
                _adventureResult.value = adventureResult
            }.onFailure {
                setThrowable(it)
            }
        }
    }

    fun fetchMyRank() {
        viewModelScope.launch {
            runCatching {
                rankRepository.getMyRank()
            }.onSuccess { rank ->
                _myRank.value = rank.rank
            }.onFailure {
                setThrowable(it)
            }
        }
    }

    private fun setThrowable(throwable: Throwable) {
        when (throwable) {
            is GameThrowable -> { _throwable.value = throwable }
        }
    }
}
