package com.now.naaga.presentation.adventureresult

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.now.domain.model.AdventureResult
import com.now.domain.repository.AdventureRepository
import com.now.domain.repository.RankRepository
import com.now.naaga.data.NaagaThrowable

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
                    .onSuccess { _adventureResult.value = it }
                    .onFailure { setErrorMessage(it) }
            },
        )
    }

    fun fetchMyRank() {
        rankRepository.getMyRank(
            callback = { result ->
                result
                    .onSuccess { _myRank.value = it.rank }
                    .onFailure { setErrorMessage(it) }
            },
        )
    }

    private fun setErrorMessage(throwable: Throwable) {
        when (throwable) {
            is NaagaThrowable.ServerConnectFailure ->
                _errorMessage.value = throwable.userMessage
        }
    }
}
