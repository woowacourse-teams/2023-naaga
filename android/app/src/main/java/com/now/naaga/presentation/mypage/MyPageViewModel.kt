package com.now.naaga.presentation.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.now.domain.model.Rank
import com.now.domain.model.Statistics
import com.now.domain.repository.RankRepository
import com.now.domain.repository.StatisticsRepository

class MyPageViewModel(
    private val rankRepository: RankRepository,
    private val statisticsRepository: StatisticsRepository,
) : ViewModel() {
    private val _rank = MutableLiveData<Rank>()
    val rank: LiveData<Rank> = _rank

    private val _statistics = MutableLiveData<Statistics>()
    val statistics: LiveData<Statistics> = _statistics

    fun fetchRank() {
        rankRepository.getMyRank { result: Result<Rank> ->
            result
                .onSuccess { rank -> _rank.value = rank }
                .onFailure { }
        }
    }

    fun fetchStatistics() {
        statisticsRepository.getMyStatistics { result: Result<Statistics> ->
            result
                .onSuccess { statistics -> _statistics.value = statistics }
                .onFailure { }
        }
    }
}
