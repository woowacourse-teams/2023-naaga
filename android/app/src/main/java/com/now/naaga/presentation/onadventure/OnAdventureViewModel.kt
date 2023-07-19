package com.now.naaga.presentation.onadventure

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.now.domain.model.Destination
import com.now.domain.repository.DestinationRepository

class OnAdventureViewModel(
    private val destinationRepository: DestinationRepository,
) : ViewModel() {

    private val _destination = MutableLiveData<Destination>()
    val destination: LiveData<Destination>
        get() = _destination

    fun getDestination() {
        _destination.value = destinationRepository.getDestination(1)
    }
}
