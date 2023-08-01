package com.now.naaga.presentation.onadventure

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import com.now.domain.model.AdventureEndType
import com.now.domain.model.AdventureStatus
import com.now.domain.model.Coordinate
import com.now.domain.model.Game
import com.now.domain.model.Hint
import com.now.domain.model.Place
import com.now.domain.repository.AdventureRepository2
import com.now.naaga.data.NaagaThrowable
import com.now.naaga.data.repository.ThirdDemoAdventureRepository

class OnAdventureViewModel2(private val adventureRepository2: AdventureRepository2) : ViewModel() {
    private val _adventure = MutableLiveData<Game>()
    val adventure: LiveData<Game> = _adventure

    private val _distance = MutableLiveData<Int>()
    val distance: LiveData<Int> = _distance
    val isNearby: LiveData<Boolean> = _distance.map { destination.value?.isNearBy(it) ?: false }

    private val _destination = MutableLiveData<Place>()
    val destination: LiveData<Place> = _destination

    private val _lastHint = MutableLiveData<Hint>()
    val lastHint: LiveData<Hint> = _lastHint

    private val _failure = MutableLiveData<Throwable>()
    val failure: LiveData<Throwable> = _failure

    fun setAdventure(game: Game) {
        _adventure.value = game
        _destination.value = game.destination
    }

    fun beginAdventure(currentCoordinate: Coordinate) {
        adventureRepository2.beginAdventure(currentCoordinate) { result: Result<Game> ->
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
        adventureRepository2.endGame(
            adventureId = adventure.value?.id ?: return,
            endType = AdventureEndType.GIVE_UP,
            coordinate = adventure.value?.startCoordinate ?: return, // 게임 포기이므로 아무 좌표나 넣어도 된다.
        ) { result: Result<AdventureStatus> ->
            result
                .onSuccess { _adventure.value = adventure.value?.copy(adventureStatus = it) }
                .onFailure { _failure.value = AdventureThrowable.GiveUpAdventureFailure() }
        }
    }

    fun openHint(coordinate: Coordinate) {
        if (isAllHintsUsed()) {
            _failure.value = AdventureThrowable.HintFailure()
            return
        }
        adventureRepository2.makeHint(
            adventureId = adventure.value?.id ?: return,
            coordinate = coordinate,
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

    fun endGame(coordinate: Coordinate) {
        adventureRepository2.endGame(
            adventureId = adventure.value?.id ?: return,
            endType = AdventureEndType.ARRIVED,
            coordinate = coordinate,
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
        val Factory = ViewModelFactory(ThirdDemoAdventureRepository())

        class ViewModelFactory(private val adventureRepository2: AdventureRepository2) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return OnAdventureViewModel(adventureRepository2) as T
            }
        }
    }
}
