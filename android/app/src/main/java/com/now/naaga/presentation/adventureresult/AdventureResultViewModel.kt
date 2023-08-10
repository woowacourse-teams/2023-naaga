package com.now.naaga.presentation.adventureresult

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.now.domain.model.AdventureResult
import com.now.domain.repository.AdventureRepository
import com.now.domain.repository.RankRepository
import com.now.naaga.data.throwable.DataThrowable
import com.now.naaga.data.throwable.DataThrowable.GameThrowable

class AdventureResultViewModel(
    private val adventureRepository: AdventureRepository,
    private val rankRepository: RankRepository,
) : ViewModel() {

    private val _adventureResult = MutableLiveData<AdventureResult>()
    val adventureResult: LiveData<AdventureResult> = _adventureResult

    private val _myRank = MutableLiveData<Int>()
    val myRank: LiveData<Int> = _myRank

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

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
        rankRepository.getMyRank(
            callback = { result ->
                result
                    .onSuccess { rank -> _myRank.value = rank.rank }
                    .onFailure { setErrorMessage(it as DataThrowable) }
            },
        )
    }

    private fun setErrorMessage(throwable: DataThrowable) {
        when (throwable) {
            is GameThrowable -> { _errorMessage.value = throwable.message }
            else -> {}
        }
    }
}
