package com.now.naaga.presentation.mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.now.domain.model.Place
import com.now.domain.model.Player
import com.now.domain.model.Statistics
import com.now.domain.model.type.OrderType
import com.now.domain.model.type.SortType
import com.now.domain.repository.PlaceRepository
import com.now.domain.repository.ProfileRepository
import com.now.domain.repository.StatisticsRepository
import com.now.naaga.data.throwable.DataThrowable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val statisticsRepository: StatisticsRepository,
    private val placeRepository: PlaceRepository,
) : ViewModel() {
    private val _profile = MutableLiveData<Player>()
    val profile: LiveData<Player> = _profile

    private val _statistics = MutableLiveData<Statistics>()
    val statistics: LiveData<Statistics> = _statistics

    private val _places = MutableLiveData<List<Place>>()
    val places: LiveData<List<Place>> = _places

    private val _throwable = MutableLiveData<DataThrowable>()
    val throwable: LiveData<DataThrowable> = _throwable

    fun fetchProfile() {
        viewModelScope.launch {
            runCatching {
                profileRepository.fetchProfile()
            }.onSuccess { profile ->
                _profile.value = profile
            }.onFailure {
                setThrowable(it)
            }
        }
    }

    fun fetchStatistics() {
        viewModelScope.launch {
            runCatching {
                statisticsRepository.getMyStatistics()
            }.onSuccess { statistics ->
                _statistics.value = statistics
            }.onFailure {
                setThrowable(it)
            }
        }
    }

    fun fetchPlaces() {
        viewModelScope.launch {
            runCatching {
                placeRepository.fetchMyPlaces(SortType.TIME.name, OrderType.DESCENDING.name)
            }.onSuccess { places ->
                _places.value = places
            }.onFailure {
                setThrowable(it)
            }
        }
    }

    private fun setThrowable(throwable: Throwable) {
        when (throwable) {
            is IOException -> { _throwable.value = DataThrowable.NetworkThrowable() }
            is DataThrowable.PlayerThrowable -> { _throwable.value = throwable }
            is DataThrowable.PlaceThrowable -> { _throwable.value = throwable }
        }
    }
}
