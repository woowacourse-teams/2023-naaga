package com.now.naaga.presentation.onadventure

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.now.domain.model.Adventure
import com.now.domain.model.AdventureStatus
import com.now.domain.model.Coordinate
import com.now.domain.model.Destination
import com.now.domain.repository.AdventureRepository
import com.now.naaga.data.NaagaThrowable

class OnAdventureViewModel(private val adventureRepository: AdventureRepository) : ViewModel() {

    private val _destination = MutableLiveData<Destination>()
    val destination: LiveData<Destination>
        get() = _destination

    private val _distance = MutableLiveData(0)
    val distance: LiveData<Int> get() = _distance

    private val _isArrived = MutableLiveData(false)
    val isArrived: LiveData<Boolean>
        get() = _isArrived

    private val _status = MutableLiveData<AdventureStatus>()
    val status: LiveData<AdventureStatus>
        get() = _status

    private val _adventureId = MutableLiveData<Long>()
    val adventureId: LiveData<Long>
        get() = _adventureId

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String>
        get() = _errorMessage

    fun fetchDestination(adventureId: Long) {
        adventureRepository.getAdventure(adventureId, callback = { result ->
            result
                .onSuccess { _destination.value = it.destination }
                .onFailure { _status.value = AdventureStatus.ERROR }
        })
    }

    fun calculateDistance(coordinate: Coordinate) {
        _distance.value = destination.value?.getDistance(coordinate)
    }

    fun checkArrived(coordinate: Coordinate) {
        _isArrived.value = destination.value?.isArrived(coordinate)
    }

    fun setAdventure(adventure: Adventure) {
        _adventureId.value = adventure.id
        _destination.value = adventure.destination
    }

    fun beginAdventure(coordinate: Coordinate) {
        adventureRepository.beginAdventure(coordinate) { result ->
            result
                .onSuccess { _adventureId.value = it }
                .onFailure { throwable ->
                    when (throwable) {
                        is NaagaThrowable.AuthenticationError ->
                            _errorMessage.value =
                                throwable.userMessage

                        is NaagaThrowable.UserError -> _errorMessage.value = throwable.userMessage
                        is NaagaThrowable.PlaceError -> _errorMessage.value = throwable.userMessage
                        is NaagaThrowable.GameError -> _errorMessage.value = throwable.userMessage
                        is NaagaThrowable.ServerConnectFailure ->
                            _errorMessage.value =
                                throwable.userMessage

                        is NaagaThrowable.NaagaUnknownError ->
                            _errorMessage.value =
                                throwable.userMessage
                    }
                }
        }
    }

    fun endAdventure(adventureId: Long, coordinate: Coordinate) {
        adventureRepository.endAdventure(
            adventureId,
            coordinate,
            callback = { result ->
                result
                    .onSuccess { adventureStatus -> _status.value = adventureStatus }
                    .onFailure { throwable ->
                        when (throwable) {
                            is NaagaThrowable.AuthenticationError ->
                                _errorMessage.value =
                                    throwable.userMessage

                            is NaagaThrowable.UserError -> _errorMessage.value = throwable.userMessage
                            is NaagaThrowable.PlaceError -> _errorMessage.value = throwable.userMessage
                            is NaagaThrowable.GameError -> _errorMessage.value = throwable.userMessage
                            is NaagaThrowable.ServerConnectFailure ->
                                _errorMessage.value =
                                    throwable.userMessage

                            is NaagaThrowable.NaagaUnknownError ->
                                _errorMessage.value =
                                    throwable.userMessage
                        }
                    }
            },
        )
    }
}
