package com.now.naaga.presentation.adventureresult

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.now.domain.model.AdventureResult
import com.now.domain.repository.AdventureRepository
import com.now.domain.repository.RankRepository
import com.now.naaga.data.repository.DefaultAdventureRepository
import com.now.naaga.data.repository.DefaultRankRepository
import com.now.naaga.data.throwable.DataThrowable
import com.now.naaga.data.throwable.DataThrowable.GameThrowable
import kotlinx.coroutines.launch

class AdventureResultViewModel(
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
        adventureRepository.fetchAdventureResult(
            adventureId,
            callback = { result ->
                result
                    .onSuccess { adventureResult -> _adventureResult.value = adventureResult }
                    .onFailure { setErrorMessage(it as DataThrowable) }
            },
        )
    }

    fun fetchMyRank() {
        viewModelScope.launch {
            runCatching {
                rankRepository.getMyRank()
            }.onSuccess { rank ->
                _myRank.value = rank.rank
            }.onFailure {
                setErrorMessage(it as DataThrowable)
            }
        }
    }

    private fun setErrorMessage(throwable: DataThrowable) {
        when (throwable) {
            is GameThrowable -> { _throwable.value = throwable }
            else -> {}
        }
    }

    companion object {
        val Factory = AdventureResultFactory(DefaultAdventureRepository(), DefaultRankRepository())

        class AdventureResultFactory(
            private val adventureRepository: AdventureRepository,
            private val rankRepository: RankRepository,
        ) :
            ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AdventureResultViewModel(adventureRepository, rankRepository) as T
            }
        }
    }
}
