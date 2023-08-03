package com.now.naaga.presentation.onadventure

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import com.now.domain.model.Adventure
import com.now.domain.model.AdventureEndType
import com.now.domain.model.AdventureStatus
import com.now.domain.model.Coordinate
import com.now.domain.model.Hint
import com.now.domain.model.Place
import com.now.domain.repository.AdventureRepository
import com.now.naaga.data.NaagaThrowable
import com.now.naaga.data.repository.DefaultAdventureRepository

class OnAdventureViewModel(private val adventureRepository: AdventureRepository) : ViewModel() {
    private val _adventure = MutableLiveData<Adventure>()
    val adventure: LiveData<Adventure> = _adventure
    val destination = DisposableLiveData<Place>()

    val myCoordinate = MutableLiveData<Coordinate>()
    val startCoordinate = DisposableLiveData<Coordinate>()

    private val _distance = MutableLiveData<Int>()
    val distance: LiveData<Int> = _distance
    val isNearby: LiveData<Boolean> = _distance.map { adventure.value?.destination?.isNearBy(it) ?: false }

    private val _lastHint = MutableLiveData<Hint>()
    val lastHint: LiveData<Hint> = _lastHint

    private val _failure = MutableLiveData<Throwable>()
    val failure: LiveData<Throwable> = _failure

    fun setAdventure(adventure: Adventure) {
        _adventure.value = adventure
        destination.setValue(adventure.destination)
    }

    fun beginAdventure(currentCoordinate: Coordinate) {
        adventureRepository.beginAdventure(currentCoordinate) { result: Result<Adventure> ->
            result.onSuccess {
                setAdventure(it)
            }.onFailure {
                _failure.value = AdventureThrowable.BeginAdventureFailure()
            }
        }
    }

    fun calculateDistance(coordinate: Coordinate) {
        _distance.value = destination.value?.getDistance(coordinate) ?: 0
    }

    fun giveUpAdventure() {
        adventureRepository.endGame(
            adventureId = adventure.value?.id ?: return,
            endType = AdventureEndType.GIVE_UP,
            coordinate = myCoordinate.value ?: return,
        ) { result: Result<AdventureStatus> ->
            result
                .onSuccess { _adventure.value = adventure.value?.copy(adventureStatus = it) }
                .onFailure { _failure.value = AdventureThrowable.GiveUpAdventureFailure() }
        }
    }

    fun openHint() {
        if (isAllHintsUsed()) {
            _failure.value = AdventureThrowable.HintFailure()
            return
        }
        adventureRepository.makeHint(
            adventureId = adventure.value?.id ?: return,
            coordinate = myCoordinate.value ?: return,
        ) { result: Result<Hint> ->
            result
                .onSuccess {
                    _adventure.value = adventure.value?.copy(hints = ((adventure.value?.hints ?: listOf()) + it))
                    _lastHint.value = it
                }
                .onFailure { _failure.value = AdventureThrowable.UnExpectedFailure() }
        }
    }

    private fun isAllHintsUsed(): Boolean {
        val hintsCount: Int = adventure.value?.hints?.size ?: 0
        return hintsCount >= MAX_HINT_COUNT
    }

    fun endAdventure() {
        adventureRepository.endGame(
            adventureId = adventure.value?.id ?: return,
            endType = AdventureEndType.ARRIVED,
            coordinate = myCoordinate.value ?: return,
        ) { result: Result<AdventureStatus> ->
            result
                .onSuccess { _adventure.value = adventure.value?.copy(adventureStatus = it) }
                .onFailure {
                    _failure.value = when (it) {
                        is NaagaThrowable.GameError -> AdventureThrowable.EndAdventureFailure()
                        else -> AdventureThrowable.UnExpectedFailure()
                    }
                }
        }
    }

    companion object {
        const val MAX_HINT_COUNT = 5
        val Factory = ViewModelFactory(DefaultAdventureRepository())

        class ViewModelFactory(private val adventureRepository: AdventureRepository) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return OnAdventureViewModel(adventureRepository) as T
            }
        }
    }
}
