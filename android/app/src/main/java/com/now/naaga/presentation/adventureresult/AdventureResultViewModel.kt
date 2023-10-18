package com.now.naaga.presentation.adventureresult

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.now.domain.model.AdventureResult
import com.now.domain.model.Preference
import com.now.domain.model.PreferenceCount
import com.now.domain.model.PreferenceState
import com.now.domain.repository.AdventureRepository
import com.now.domain.repository.PlaceRepository
import com.now.domain.repository.RankRepository
import com.now.naaga.data.throwable.DataThrowable
import com.now.naaga.data.throwable.DataThrowable.GameThrowable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class AdventureResultViewModel @Inject constructor(
    private val adventureRepository: AdventureRepository,
    private val rankRepository: RankRepository,
    private val placeRepository: PlaceRepository,
) : ViewModel() {
    private val _adventureResult = MutableLiveData<AdventureResult>()
    val adventureResult: LiveData<AdventureResult> = _adventureResult

    private val _myRank = MutableLiveData<Int>()
    val myRank: LiveData<Int> = _myRank

    private val _preference = MutableLiveData<Preference>()
    val preference: LiveData<Preference> = _preference

    private val _throwable = MutableLiveData<DataThrowable>()
    val throwable: LiveData<DataThrowable> = _throwable

    fun changePreference(newState: PreferenceState) {
        _preference.value = preference.value?.select(newState)
        if (preference.value?.state == PreferenceState.NONE) {
            deletePreference()
            return
        }
        postPreference()
    }

    fun fetchGameResult(adventureId: Long) {
        viewModelScope.launch {
            runCatching {
                adventureRepository.fetchAdventureResult(adventureId)
            }.onSuccess { adventureResult ->
                _adventureResult.value = adventureResult
            }.onFailure {
                setThrowable(it)
            }
        }
    }

    fun fetchMyRank() {
        viewModelScope.launch {
            runCatching {
                rankRepository.getMyRank()
            }.onSuccess { rank ->
                _myRank.value = rank.rank
            }.onFailure {
                setThrowable(it)
            }
        }
    }

    fun fetchPreference() {
        viewModelScope.launch {
            runCatching {
                val placeId =
                    requireNotNull(adventureResult.value) { "adventureResult가 null입니다." }.destination.id.toInt()
                val deferredLikeCount = async { placeRepository.getLikeCount(placeId) }
                val deferredPreferenceState = async { placeRepository.getMyPreference(placeId) }

                Preference(
                    state = deferredPreferenceState.await(),
                    likeCount = PreferenceCount(deferredLikeCount.await()),
                )
            }.onSuccess {
                _preference.value = it
            }.onFailure {
                setThrowable(it)
            }
        }
    }

    private fun postPreference() {
        viewModelScope.launch {
            runCatching {
                placeRepository.postPreference(
                    requireNotNull(adventureResult.value) { "adventureResult가 null입니다." }.destination.id.toInt(),
                    requireNotNull(preference.value) { "preference가 null입니다." }.state,
                )
            }.onSuccess {
                // post 응답이 성공적으로 왔는데 내가 보낸 것과 다른게 온 경우. 즉 말이 안되는 경우
                if (preference.value?.state != it) {
                    _preference.value = Preference(state = it)
                }
            }.onFailure {
                _preference.value = preference.value?.revert()
                setThrowable(it)
            }
        }
    }

    private fun deletePreference() {
        viewModelScope.launch {
            val placeId = requireNotNull(adventureResult.value) { "adventureResult가 null입니다." }.destination.id.toInt()
            runCatching {
                placeRepository.deletePreference(placeId)
            }.onFailure {
                _preference.value = preference.value?.revert()
                setThrowable(it)
            }
        }
    }

    private fun setThrowable(throwable: Throwable) {
        when (throwable) {
            is IOException -> _throwable.value = DataThrowable.NetworkThrowable()
            is GameThrowable -> _throwable.value = throwable
        }
    }
}
