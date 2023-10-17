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
        println("[asdf] changePreference: ${preference.value?.state}")
        if (preference.value?.state == PreferenceState.NONE) {
            println("[asdf] in if")
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
                setErrorMessage(it as DataThrowable)
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
                setErrorMessage(it as DataThrowable)
            }
        }
    }

    fun fetchPreference() {
        println("[asdf] fetchPreference called")
        viewModelScope.launch {
            kotlin.runCatching {
                val placeId = (adventureResult.value ?: return@launch).destination.id.toInt()
                val deferredLikeCount = async { placeRepository.getLikeCount(placeId) }
                val deferredPreferenceState = async { placeRepository.getMyPreference(placeId) }

                Preference(
                    state = deferredPreferenceState.await(),
                    likeCount = PreferenceCount(deferredLikeCount.await()),
                )
            }.onSuccess {
                _preference.value = it
                println("[asdf] fetchPreference succeed")
            }.onFailure {
                println("[asdf] fetchPreference failed: $it")
            }
        }
    }

    private fun postPreference() {
        viewModelScope.launch {
            runCatching {
                placeRepository.postPreference(
                    adventureResult.value?.destination?.id?.toInt() ?: return@launch,
                    preference.value?.state ?: return@launch,
                )
            }.onSuccess {
                if (preference.value?.state != it) {
                    _preference.value = Preference(state = it)
                    // 에러 로그
                }
            }.onFailure {
                _preference.value = preference.value?.revert()
            }
        }
    }

    private fun deletePreference() {
        viewModelScope.launch {
            val placeId = (adventureResult.value!!).destination.id.toInt()
            runCatching {
                println("[asdf] in runCatching")
                placeRepository.deletePreference(placeId)
            }.onSuccess {
                println("[asdf] onSuccess")
            }.onFailure {
                println("[asdf] onFailure: $it")
                _preference.value = preference.value?.revert()
            }
        }
    }

    private fun setErrorMessage(throwable: DataThrowable) {
        when (throwable) {
            is GameThrowable -> {
                _throwable.value = throwable
            }

            else -> {}
        }
    }
}
