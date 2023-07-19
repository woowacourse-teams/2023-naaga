package com.now.naaga.presentation.onadventure

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.now.domain.model.Coordinate
import com.now.domain.model.Destination
import com.now.domain.repository.DestinationRepository

class OnAdventureViewModel(
    private val destinationRepository: DestinationRepository,
) : ViewModel() {

    private val _destination = MutableLiveData<Destination>()
    val destination: LiveData<Destination>
        get() = _destination

    private val _distance = MutableLiveData(0)
    val distance: LiveData<Int> get() = _distance

    init {
        getDestination()
    }

    private fun getDestination() {
        _destination.value = destinationRepository.getDestination(1)
    }

    fun calculateDistance(coordinate: Coordinate) {
        _distance.value = destination.value?.getDistance(coordinate)
    }
}
