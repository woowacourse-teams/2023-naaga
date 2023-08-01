package com.now.naaga.presentation.rank

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.now.domain.model.Rank
import com.now.domain.repository.RankRepository
import com.now.naaga.data.NaagaThrowable

class RankViewModel(private val rankRepository: RankRepository) : ViewModel() {
    private val _myName = MutableLiveData<String>()
    val myName: LiveData<String> = _myName

    private val _myScore = MutableLiveData<Int>()
    val myScore: LiveData<Int> = _myScore

    private val _myRank = MutableLiveData<Int>()
    val myRank: LiveData<Int> = _myRank

    private val _ranks = MutableLiveData<List<Rank>>()
    val ranks: LiveData<List<Rank>> = _ranks

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun fetchMyRank() {
        rankRepository.getMyRank(
            callback = { result ->
                result
                    .onSuccess {
                        _myName.value = it.player.nickname
                        _myScore.value = it.player.score
                        _myRank.value = it.rank
                    }
                    .onFailure { throwable ->
                        when (throwable) {
                            is NaagaThrowable.ServerConnectFailure ->
                                _errorMessage.value =
                                    throwable.userMessage
                        }
                    }
            },
        )
    }

    fun fetchRanks() {
        rankRepository.getAllRanks(
            SORT_BY_QUERY,
            ORDER_QUERY,
            callback = { result ->
                result
                    .onSuccess { _ranks.value = it }
                    .onFailure { throwable ->
                        when (throwable) {
                            is NaagaThrowable.ServerConnectFailure ->
                                _errorMessage.value =
                                    throwable.userMessage
                        }
                    }
            },
        )
    }

    companion object {
        private const val SORT_BY_QUERY = "rank"
        private const val ORDER_QUERY = "ascending"
    }
}
