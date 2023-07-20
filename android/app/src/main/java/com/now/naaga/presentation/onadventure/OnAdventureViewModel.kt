package com.now.naaga.presentation.onadventure

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.now.domain.model.AdventureStatus
import com.now.domain.model.Coordinate
import com.now.domain.model.Destination
import com.now.domain.repository.AdventureRepository

class OnAdventureViewModel(
    private val adventureRepository: AdventureRepository,
) : ViewModel() {

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

    fun fetchDestination(adventureId: Long) {
        adventureRepository.getAdventure(adventureId, callback = { result ->
            result
                .onSuccess { _destination.value = it.destination }
                .onFailure {
                    _status.value = AdventureStatus.ERROR
                }
        })
    }

    fun calculateDistance(coordinate: Coordinate) {
        _distance.value = destination.value?.getDistance(coordinate)
    }

    fun checkArrived(coordinate: Coordinate) {
        _isArrived.value = destination.value?.isArrived(coordinate)
    }
}
