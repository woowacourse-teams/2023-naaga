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
import com.now.domain.model.RemainingTryCount
import com.now.domain.repository.AdventureRepository
import com.now.naaga.data.repository.DefaultAdventureRepository
import com.now.naaga.data.throwable.DataThrowable
import com.now.naaga.data.throwable.DataThrowable.Companion.hintThrowable
import com.now.naaga.data.throwable.DataThrowable.GameThrowable

class OnAdventureViewModel(private val adventureRepository: AdventureRepository) : ViewModel() {
    private val _adventure = MutableLiveData<Adventure>()
    val adventure: LiveData<Adventure> = _adventure
    val hints = DisposableLiveData<List<Hint>>(_adventure.map { it.hints })
    val remainingHintCount = DistinctChildLiveData<Int>(adventure.map { getRemainingHintCount() })

    val myCoordinate = MutableLiveData<Coordinate>()
    val startCoordinate = DisposableLiveData<Coordinate>(myCoordinate)
    val distance: LiveData<Int> = myCoordinate.map { adventure.value?.destination?.getDistance(it) ?: return@map 0 }
    val isNearby: LiveData<Boolean> =
        myCoordinate.map { adventure.value?.destination?.isNearBy(it) ?: return@map false }

    private val _lastHint = MutableLiveData<Hint>()
    val lastHint: LiveData<Hint> = _lastHint

    private val _error = MutableLiveData<DataThrowable>()
    val error: LiveData<DataThrowable> = _error

    fun setAdventure(adventure: Adventure) {
        _adventure.value = adventure
    }

    fun beginAdventure(currentCoordinate: Coordinate) {
        adventureRepository.beginAdventure(currentCoordinate) { result: Result<Adventure> ->
            result
                .onSuccess { setAdventure(it) }
                .onFailure { setError(it as DataThrowable) }
        }
    }

    fun giveUpAdventure() {
        adventureRepository.endGame(
            adventureId = adventure.value?.id ?: return,
            endType = AdventureEndType.GIVE_UP,
            coordinate = myCoordinate.value ?: return,
        ) { result: Result<AdventureStatus> ->
            result
                .onSuccess { _adventure.value = adventure.value?.copy(adventureStatus = it) }
                .onFailure { setError(it as DataThrowable) }
        }
    }

    fun openHint() {
        if (isAllHintsUsed()) {
            setError(hintThrowable)
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
                .onFailure { setError(it as DataThrowable) }
        }
    }

    private fun isAllHintsUsed(): Boolean {
        return getRemainingHintCount() <= 0
    }

    private fun getRemainingHintCount(): Int {
        val usedHintCount = adventure.value?.hints?.size ?: 0
        return (RemainingTryCount(MAX_HINT_COUNT) - usedHintCount).toInt()
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
                    when ((it as DataThrowable).code) {
                        NOT_ARRIVED -> {
                            val currentRemainingTryCount = adventure.value?.remainingTryCount ?: return@onFailure
                            _adventure.value = adventure.value?.copy(remainingTryCount = currentRemainingTryCount - 1)
                        }

                        TRY_COUNT_OVER ->
                            _adventure.value = adventure.value?.copy(adventureStatus = AdventureStatus.DONE)
                    }
                    setError(it)
                }
        }
    }

    private fun setError(throwable: DataThrowable) {
        when (throwable) {
            is GameThrowable -> _error.value = throwable
            else -> {}
        }
    }

    companion object {
        const val MAX_HINT_COUNT = 5
        const val NO_DESTINATION = 406
        const val NOT_ARRIVED = 415
        const val TRY_COUNT_OVER = 416
        val Factory = ViewModelFactory(DefaultAdventureRepository())

        class ViewModelFactory(private val adventureRepository: AdventureRepository) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return OnAdventureViewModel(adventureRepository) as T
            }
        }
    }
}
