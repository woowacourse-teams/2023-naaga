package com.now.naaga.presentation.rank

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.now.domain.model.OrderType
import com.now.domain.model.Rank
import com.now.domain.model.SortType
import com.now.domain.repository.RankRepository
import com.now.naaga.data.repository.DefaultRankRepository
import com.now.naaga.data.throwable.DataThrowable
import com.now.naaga.data.throwable.DataThrowable.PlayerThrowable

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
                    .onSuccess { rank ->
                        _myName.value = rank.player.nickname
                        _myScore.value = rank.player.score
                        _myRank.value = rank.rank
                    }
                    .onFailure { setErrorMessage(it as DataThrowable) }
            },
        )
    }

    fun fetchRanks() {
        rankRepository.getAllRanks(
            SortType.RANK.name,
            OrderType.ASCENDING.name,
            callback = { result ->
                result
                    .onSuccess { ranks -> _ranks.value = ranks }
                    .onFailure { setErrorMessage(it as DataThrowable) }
            },
        )
    }

    private fun setErrorMessage(throwable: DataThrowable) {
        when (throwable) {
            is PlayerThrowable -> { _errorMessage.value = throwable.message }
            else -> {}
        }
    }

    companion object {
        val Factory = RankFactory(DefaultRankRepository())

        class RankFactory(private val rankRepository: RankRepository) :
            ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RankViewModel(rankRepository) as T
            }
        }
    }
}
