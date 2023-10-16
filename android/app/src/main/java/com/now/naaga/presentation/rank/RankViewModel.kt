package com.now.naaga.presentation.rank

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.now.domain.model.Rank
import com.now.domain.model.type.OrderType
import com.now.domain.model.type.SortType
import com.now.domain.repository.RankRepository
import com.now.naaga.data.throwable.DataThrowable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class RankViewModel @Inject constructor(private val rankRepository: RankRepository) : ViewModel() {
    private val _myName = MutableLiveData<String>()
    val myName: LiveData<String> = _myName

    private val _myScore = MutableLiveData<Int>()
    val myScore: LiveData<Int> = _myScore

    private val _myRank = MutableLiveData<Int>()
    val myRank: LiveData<Int> = _myRank

    private val _ranks = MutableLiveData<List<Rank>>()
    val ranks: LiveData<List<Rank>> = _ranks

    private val _throwable = MutableLiveData<DataThrowable>()
    val throwable: LiveData<DataThrowable> = _throwable

    fun fetchMyRank() {
        viewModelScope.launch {
            runCatching {
                rankRepository.getMyRank()
            }.onSuccess { rank ->
                _myName.value = rank.player.nickname
                _myScore.value = rank.player.score
                _myRank.value = rank.rank
            }.onFailure {
                setThrowable(it)
            }
        }
    }

    fun fetchRanks() {
        viewModelScope.launch {
            runCatching {
                rankRepository.getAllRanks(SortType.RANK.name, OrderType.ASCENDING.name)
            }.onSuccess { ranks ->
                _ranks.value = ranks
            }.onFailure {
                setThrowable(it)
            }
        }
    }

    private fun setThrowable(throwable: Throwable) {
        when (throwable) {
            is IOException -> { _throwable.value = DataThrowable.NetworkThrowable() }
            is DataThrowable.PlayerThrowable -> { _throwable.value = throwable }
        }
    }
}
