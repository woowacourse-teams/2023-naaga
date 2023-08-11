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
import com.now.domain.repository.AdventureRepository
import com.now.naaga.data.repository.DefaultAdventureRepository
import com.now.naaga.data.throwable.DataThrowable
import com.now.naaga.data.throwable.DataThrowable.Companion.hintThrowable
import com.now.naaga.data.throwable.DataThrowable.GameThrowable
import com.now.naaga.data.throwable.DataThrowable.UniversalThrowable

class OnAdventureViewModel(private val adventureRepository: AdventureRepository) : ViewModel() {
    private val _adventure = MutableLiveData<Adventure>()
    val adventure: LiveData<Adventure> = _adventure
    val hints = DisposableLiveData<List<Hint>>(_adventure.map { it.hints })

    val myCoordinate = MutableLiveData<Coordinate>()
    val startCoordinate = DisposableLiveData<Coordinate>(myCoordinate)

    private val _distance = MutableLiveData<Int>()
    val distance: LiveData<Int> = _distance
    val isNearby: LiveData<Boolean> = _distance.map { adventure.value?.destination?.isNearBy(it) ?: false }

    private val _lastHint = MutableLiveData<Hint>()
    val lastHint: LiveData<Hint> = _lastHint

    private val _throwable = MutableLiveData<DataThrowable>()
    val throwable: LiveData<DataThrowable> = _throwable

    fun setAdventure(adventure: Adventure) {
        _adventure.value = adventure
    }

    fun beginAdventure(currentCoordinate: Coordinate) {
        adventureRepository.beginAdventure(currentCoordinate) { result: Result<Adventure> ->
            result
                .onSuccess { setAdventure(it) }
                .onFailure { setErrorMessage(it as DataThrowable) }
        }
    }

    fun calculateDistance(coordinate: Coordinate) {
        _distance.value = adventure.value?.destination?.getDistance(coordinate) ?: 0
    }

    fun giveUpAdventure() {
        adventureRepository.endGame(
            adventureId = adventure.value?.id ?: return,
            endType = AdventureEndType.GIVE_UP,
            coordinate = myCoordinate.value ?: return,
        ) { result: Result<AdventureStatus> ->
            result
                .onSuccess { _adventure.value = adventure.value?.copy(adventureStatus = it) }
                .onFailure { setErrorMessage(it as DataThrowable) }
        }
    }

    fun openHint() {
        if (isAllHintsUsed()) {
            setErrorMessage(hintThrowable)
            return
        }
        adventureRepository.makeHint(
            adventureId = adventure.value?.id ?: return,
            coordinate = myCoordinate.value ?: return,
        ) { result: Result<Hint> ->
            result
                .onSuccess { hint ->
                    _adventure.value = adventure.value?.copy(hints = ((adventure.value?.hints ?: listOf()) + hint))
                    _lastHint.value = hint
                }
                .onFailure { setErrorMessage(it as DataThrowable) }
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
                .onFailure { setErrorMessage(it as DataThrowable) }
        }
    }

    private fun setErrorMessage(throwable: DataThrowable) {
        when (throwable) {
            is UniversalThrowable -> { _throwable.value = throwable }
            is GameThrowable -> { _throwable.value = throwable }
            else -> {}
        }
    }

    companion object {
        const val MAX_HINT_COUNT = 3
        val Factory = ViewModelFactory(DefaultAdventureRepository())

        class ViewModelFactory(private val adventureRepository: AdventureRepository) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return OnAdventureViewModel(adventureRepository) as T
            }
        }
    }
}
