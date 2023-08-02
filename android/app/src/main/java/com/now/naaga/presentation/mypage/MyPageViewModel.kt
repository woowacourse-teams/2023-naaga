package com.now.naaga.presentation.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.now.domain.model.OrderType
import com.now.domain.model.Place
import com.now.domain.model.Rank
import com.now.domain.model.SortType
import com.now.domain.model.Statistics
import com.now.domain.repository.PlaceRepository
import com.now.domain.repository.RankRepository
import com.now.domain.repository.StatisticsRepository
import com.now.naaga.data.NaagaThrowable

class MyPageViewModel(
    private val rankRepository: RankRepository,
    private val statisticsRepository: StatisticsRepository,
    private val placeRepository: PlaceRepository,
) : ViewModel() {
    private val _rank = MutableLiveData<Rank>()
    val rank: LiveData<Rank> = _rank

    private val _statistics = MutableLiveData<Statistics>()
    val statistics: LiveData<Statistics> = _statistics

    private val _places = MutableLiveData<List<Place>>()
    val places: LiveData<List<Place>> = _places

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun fetchRank() {
        rankRepository.getMyRank { result: Result<Rank> ->
            result
                .onSuccess { rank -> _rank.value = rank }
                .onFailure { setErrorMessage(it) }
        }
    }

    fun fetchStatistics() {
        statisticsRepository.getMyStatistics { result: Result<Statistics> ->
            result
                .onSuccess { statistics -> _statistics.value = statistics }
                .onFailure { setErrorMessage(it) }
        }
    }

    fun fetchPlaces() {
        placeRepository.fetchMyPlaces(SortType.RANK.name, OrderType.DESCENDING.name) { result ->
            result
                .onSuccess { _places.value = it }
                .onFailure { setErrorMessage(it) }
        }
    }

    private fun setErrorMessage(throwable: Throwable) {
        when (throwable) {
            is NaagaThrowable.ServerConnectFailure ->
                _errorMessage.value = throwable.userMessage
        }
    }
}
