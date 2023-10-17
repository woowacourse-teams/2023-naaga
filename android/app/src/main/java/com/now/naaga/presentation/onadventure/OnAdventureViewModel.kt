package com.now.naaga.presentation.onadventure

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.now.domain.model.Adventure
import com.now.domain.model.AdventureStatus
import com.now.domain.model.Coordinate
import com.now.domain.model.Hint
import com.now.domain.model.RemainingTryCount
import com.now.domain.model.letter.ClosedLetter
import com.now.domain.model.letter.OpenLetter
import com.now.domain.model.type.AdventureEndType
import com.now.domain.repository.AdventureRepository
import com.now.domain.repository.LetterRepository
import com.now.naaga.data.throwable.DataThrowable
import com.now.naaga.data.throwable.DataThrowable.Companion.hintThrowable
import com.now.naaga.data.throwable.DataThrowable.GameThrowable
import com.now.naaga.data.throwable.DataThrowable.UniversalThrowable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnAdventureViewModel @Inject constructor(
    private val adventureRepository: AdventureRepository,
    private val letterRepository: LetterRepository,
) : ViewModel() {
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

    val letters: LiveData<List<ClosedLetter>> = liveData {
        while (true) {
            val letters = myCoordinate.value.let {
                if (it == null) {
                    emptyList()
                } else {
                    letterRepository.fetchNearbyLetters(
                        latitude = it.latitude,
                        longitude = it.longitude,
                    )
                }
            }
            isLetterNearBy(letters)
            emit(letters)
            delay(15000)
        }
    }

    private val _letter = MutableLiveData<OpenLetter>()
    val letter: LiveData<OpenLetter> = _letter

    private val _error = MutableLiveData<DataThrowable>()
    val error: LiveData<DataThrowable> = _error

    fun setAdventure(adventure: Adventure) {
        _adventure.value = adventure
    }

    fun beginAdventure(currentCoordinate: Coordinate) {
        viewModelScope.launch {
            runCatching {
                adventureRepository.beginAdventure(currentCoordinate)
            }.onSuccess {
                setAdventure(it)
            }.onFailure {
                setError(it as DataThrowable)
            }
        }
    }

    fun giveUpAdventure() {
        viewModelScope.launch {
            runCatching {
                adventureRepository.endGame(
                    adventureId = adventure.value?.id ?: throw IllegalStateException(ADVENTURE_IS_NULL),
                    endType = AdventureEndType.GIVE_UP,
                    coordinate = myCoordinate.value ?: throw IllegalStateException(MY_COORDINATE_IS_NULL),
                )
            }.onSuccess { status: AdventureStatus ->
                _adventure.value = adventure.value?.copy(adventureStatus = status)
            }.onFailure {
                setError(it as DataThrowable)
            }
        }
    }

    fun openHint() {
        if (isAllHintsUsed()) {
            setError(hintThrowable)
            return
        }
        viewModelScope.launch {
            runCatching {
                adventureRepository.makeHint(
                    adventureId = adventure.value?.id ?: throw IllegalStateException(ADVENTURE_IS_NULL),
                    coordinate = myCoordinate.value ?: throw IllegalStateException(MY_COORDINATE_IS_NULL),
                )
            }.onSuccess { hint: Hint ->
                _adventure.value = adventure.value?.copy(hints = ((adventure.value?.hints ?: listOf()) + hint))
                _lastHint.value = hint
            }.onFailure {
                setError(it as DataThrowable)
            }
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
        viewModelScope.launch {
            runCatching {
                adventureRepository.endGame(
                    adventureId = adventure.value?.id ?: throw IllegalStateException(ADVENTURE_IS_NULL),
                    endType = AdventureEndType.ARRIVED,
                    coordinate = myCoordinate.value ?: throw IllegalStateException(MY_COORDINATE_IS_NULL),
                )
            }.onSuccess {
                _adventure.value = adventure.value?.copy(adventureStatus = it)
            }.onFailure {
                when ((it as DataThrowable).code) {
                    TRY_COUNT_OVER -> _adventure.value = adventure.value?.copy(adventureStatus = AdventureStatus.DONE)
                    NOT_ARRIVED -> {
                        val currentRemainingTryCount = adventure.value?.remainingTryCount ?: return@onFailure
                        _adventure.value = adventure.value?.copy(remainingTryCount = currentRemainingTryCount - 1)
                    }
                }
                setError(it)
            }
        }
    }

    private fun isLetterNearBy(letters: List<ClosedLetter>) {
        letters.forEach { letter ->
            myCoordinate.value?.let { letter.isNearBy(it) }
        }
    }

    fun getLetter(id: Long) {
        viewModelScope.launch {
            runCatching {
                letterRepository.fetchLetter(id)
            }.onSuccess { letter ->
                _letter.value = letter
            }.onFailure {
                setError(it as DataThrowable)
            }
        }
    }

    private fun setError(throwable: DataThrowable) {
        when (throwable) {
            is GameThrowable -> _error.value = throwable
            is UniversalThrowable -> _error.value = throwable
            else -> {}
        }
    }

    companion object {
        const val MAX_HINT_COUNT = 5
        const val NO_DESTINATION = 406
        const val NOT_ARRIVED = 415
        const val TRY_COUNT_OVER = 416
        private const val ERROR_PREFIX = "[ERROR] OnAdventureViewModel:"
        private const val ADVENTURE_IS_NULL = "$ERROR_PREFIX adventure가 널입니다."
        private const val MY_COORDINATE_IS_NULL = "$ERROR_PREFIX myCoordinate가 널입니다."
    }
}
