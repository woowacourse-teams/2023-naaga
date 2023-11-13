package com.now.naaga.presentation.onadventure

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.now.domain.model.Adventure
import com.now.domain.model.AdventureStatus
import com.now.domain.model.Coordinate
import com.now.domain.model.Hint
import com.now.domain.model.RemainingTryCount
import com.now.domain.model.letter.LetterPreview
import com.now.domain.model.type.AdventureEndType
import com.now.domain.repository.AdventureRepository
import com.now.domain.repository.LetterRepository
import com.now.naaga.data.throwable.DataThrowable
import com.now.naaga.data.throwable.DataThrowable.Companion.hintThrowable
import com.now.naaga.data.throwable.DataThrowable.GameThrowable
import com.now.naaga.data.throwable.DataThrowable.UniversalThrowable
import com.now.naaga.presentation.uimodel.mapper.toUiModel
import com.now.naaga.presentation.uimodel.model.LetterUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class OnAdventureViewModel @Inject constructor(
    private val adventureRepository: AdventureRepository,
    private val letterRepository: LetterRepository,
) : ViewModel() {
    private val _adventure = MutableLiveData<Adventure>()
    val adventure: LiveData<Adventure> = _adventure
    val hints = DisposableLiveData<List<Hint>>(_adventure.map { it.hints })
    val remainingHintCount: Int
        get() = (RemainingTryCount(MAX_HINT_COUNT) - (adventure.value?.hints?.size ?: 0)).toInt()

    val myCoordinate = MutableLiveData<Coordinate>()
    val startCoordinate = DisposableLiveData<Coordinate>(myCoordinate)
    val distance: LiveData<Int> = myCoordinate.map { adventure.value?.destination?.getDistance(it) ?: return@map 0 }
    val isNearby: LiveData<Boolean> =
        myCoordinate.map { adventure.value?.destination?.isNearBy(it) ?: return@map false }

    private var _lastHint = MutableLiveData<Hint>()
    val lastHint: LiveData<Hint> = _lastHint

    private val _letters = MutableLiveData<List<LetterPreview>>()
    val letters: LiveData<List<LetterPreview>> = _letters

    private val _letter = MutableLiveData<LetterUiModel>()

    val letter: LiveData<LetterUiModel> = _letter
    private val _throwable = MutableLiveData<DataThrowable>()

    val throwable: LiveData<DataThrowable> = _throwable
    private val _isSendLetterSuccess = MutableLiveData<Boolean>()

    val isSendLetterSuccess: LiveData<Boolean> = _isSendLetterSuccess
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
                setThrowable(it)
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
                setThrowable(it)
            }
        }
    }

    fun fetchLetters() {
        viewModelScope.launch {
            while (true) {
                val coordinate = requireNotNull(myCoordinate.value) { "나의 좌표가 null 입니다." }
                runCatching {
                    letterRepository.fetchNearbyLetters(coordinate.latitude, coordinate.longitude)
                }.onSuccess {
                    _letters.value = it
                }.onFailure {
                    setThrowable(it)
                }
                delay(5000L)
            }
        }
    }

    fun openHint() {
        if (isAllHintsUsed()) {
            setThrowable(hintThrowable)
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
                setThrowable(it)
            }
        }
    }

    private fun isAllHintsUsed(): Boolean {
        return remainingHintCount <= 0
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
                setThrowable(it)
            }
        }
    }

    private fun handleGameThrowable(throwable: GameThrowable) {
        when (throwable.code) {
            TRY_COUNT_OVER -> {
                _adventure.value = adventure.value?.copy(adventureStatus = AdventureStatus.DONE)
                _throwable.value = throwable
            }

            NOT_ARRIVED -> {
                val currentRemainingTryCount = adventure.value?.remainingTryCount ?: return
                _adventure.value = adventure.value?.copy(remainingTryCount = currentRemainingTryCount - 1)
                _throwable.value = throwable
            }

            else -> {
                _throwable.value = throwable
            }
        }
    }

    fun getLetter(id: Long) {
        viewModelScope.launch {
            runCatching {
                letterRepository.fetchLetter(id)
            }.onSuccess { letter ->
                _letter.value = letter.toUiModel()
            }.onFailure {
                setThrowable(it)
            }
        }
    }

    fun sendLetter(message: String) {
        viewModelScope.launch {
            runCatching {
                myCoordinate.value?.let { letterRepository.postLetter(message, it.latitude, it.longitude) }
            }.onSuccess {
                _isSendLetterSuccess.value = true
            }.onFailure {
                setThrowable(it)
            }
        }
    }

    private fun setThrowable(throwable: Throwable) {
        when (throwable) {
            is IOException -> _throwable.value = DataThrowable.NetworkThrowable()
            is GameThrowable -> handleGameThrowable(throwable)
            is UniversalThrowable -> _throwable.value = throwable
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
