package com.now.naaga.presentation.adventureresult

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.now.domain.model.AdventureResult
import com.now.domain.repository.AdventureRepository2
import com.now.domain.repository.RankRepository
import com.now.naaga.data.NaagaThrowable

class AdventureResultViewModel(
    private val adventureRepository: AdventureRepository2,
    private val rankRepository: RankRepository,
) : ViewModel() {

    private val _adventureResult = MutableLiveData<AdventureResult>()
    val adventureResult: LiveData<AdventureResult> = _adventureResult

    private val _myRank = MutableLiveData<Int>()
    val myRank: LiveData<Int> = _myRank

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun fetchGameResult(gameId: Long) {
        adventureRepository.fetchAdventureResult(
            TEMP_MOCK_GAME_ID,
            callback = { result ->
                result
                    .onSuccess { _adventureResult.value = it }
                    .onFailure {
                        setErrorMessage(it)
                        Log.d("test", "게임 결과 통신 실패")
                    }
            },
        )
    }

    private fun setErrorMessage(throwable: Throwable) {
        when (throwable) {
            is NaagaThrowable.ServerConnectFailure ->
                _errorMessage.value = throwable.userMessage
        }
    }

    companion object {
        private const val TEMP_MOCK_GAME_ID = 1L
    }
}
