package com.now.naaga.presentation.mypage

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.kakao.sdk.user.UserApiClient
import com.now.domain.model.OrderType
import com.now.domain.model.Place
import com.now.domain.model.Rank
import com.now.domain.model.SortType
import com.now.domain.model.Statistics
import com.now.domain.repository.PlaceRepository
import com.now.domain.repository.RankRepository
import com.now.domain.repository.StatisticsRepository
import com.now.naaga.data.repository.DefaultPlaceRepository
import com.now.naaga.data.repository.DefaultRankRepository
import com.now.naaga.data.repository.DefaultStatisticsRepository
import com.now.naaga.data.throwable.DataThrowable
import com.now.naaga.data.throwable.DataThrowable.PlaceThrowable
import com.now.naaga.data.throwable.DataThrowable.PlayerThrowable

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

    private val _nickname = MutableLiveData<String>()
    val nickname: LiveData<String> = _nickname

    fun fetchRank() {
        rankRepository.getMyRank { result: Result<Rank> ->
            result
                .onSuccess { rank -> _rank.value = rank }
                .onFailure { setErrorMessage(it as DataThrowable) }
        }
    }

    fun fetchStatistics() {
        statisticsRepository.getMyStatistics { result: Result<Statistics> ->
            result
                .onSuccess { statistics -> _statistics.value = statistics }
                .onFailure { setErrorMessage(it as DataThrowable) }
        }
    }

    fun fetchPlaces() {
        placeRepository.fetchMyPlaces(SortType.TIME.name, OrderType.DESCENDING.name) { result ->
            result
                .onSuccess { places -> _places.value = places }
                .onFailure { setErrorMessage(it as DataThrowable) }
        }
    }

    fun fetchNickname() {
        UserApiClient.instance.me { user, error ->
            if (error != null) {
                Log.d(KAKAO_USER_INFO_LOG_TAG, KAKAO_USER_INFO_FAIL_MESSAGE + error)
            } else if (user != null) {
                Log.d(KAKAO_USER_INFO_LOG_TAG, KAKAO_USER_INFO_SUCCESS_MESSAGE)
                _nickname.value = user.kakaoAccount?.profile?.nickname.toString()
            }
        }
    }

    private fun setErrorMessage(throwable: Throwable) {
        when (throwable) {
            is PlayerThrowable -> { _errorMessage.value = throwable.message }
            is PlaceThrowable -> { _errorMessage.value = throwable.message }
            else -> {}
        }
    }

    companion object {
        private const val KAKAO_USER_INFO_LOG_TAG = "kakao user"
        private const val KAKAO_USER_INFO_FAIL_MESSAGE = "사용자 정보 요청 실패"
        private const val KAKAO_USER_INFO_SUCCESS_MESSAGE = "사용자 정보 요청 성공"

        val Factory = MyPageFactory(DefaultRankRepository(), DefaultStatisticsRepository(), DefaultPlaceRepository())

        class MyPageFactory(
            private val rankRepository: RankRepository,
            private val statisticsRepository: StatisticsRepository,
            private val placeRepository: PlaceRepository,
        ) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MyPageViewModel(rankRepository, statisticsRepository, placeRepository) as T
            }
        }
    }
}
