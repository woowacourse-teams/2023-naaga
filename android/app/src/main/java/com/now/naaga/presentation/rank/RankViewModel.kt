package com.now.naaga.presentation.rank

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.now.domain.model.Player
import com.now.domain.model.Rank

class RankViewModel : ViewModel() {
    private val _myName = MutableLiveData<String>()
    val myName: LiveData<String> = _myName

    private val _myScore = MutableLiveData<Int>()
    val myScore: LiveData<Int> = _myScore

    private val _myRank = MutableLiveData<Int>()
    val myRank: LiveData<Int> = _myRank

    private val _ranks = MutableLiveData<List<Rank>>()
    val ranks: LiveData<List<Rank>> = _ranks

    fun fetchMyRank() {
        _myName.value = TEMP_MOCK_MY_NAME_DATA
        _myScore.value = TEMP_MOCK_MY_SCORE_DATA
        _myRank.value = TEMP_MOCK_MY_RANK_DATA
    }

    fun fetchRanks() {
        _ranks.value = TEMP_MOCK_RANKS_DATA
    }

    companion object {
        private const val TEMP_MOCK_MY_NAME_DATA = "뽀또"
        private const val TEMP_MOCK_MY_SCORE_DATA = 12300
        private const val TEMP_MOCK_MY_RANK_DATA = 1

        private val TEMP_MOCK_RANKS_DATA = List(10) {
            Rank(Player(id = 1, nickname = "뽀또", score = 12300), rank = 1, percent = 1.0)
        }
    }
}
