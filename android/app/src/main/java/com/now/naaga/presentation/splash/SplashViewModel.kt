package com.now.naaga.presentation.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.now.domain.model.Adventure
import com.now.domain.model.AdventureStatus
import com.now.domain.model.Coordinate
import com.now.domain.model.Destination
import com.now.domain.repository.AdventureRepository

class SplashViewModel(private val adventureRepository: AdventureRepository) : ViewModel() {
    private val _adventure = MutableLiveData<Adventure>()
    val adventure: LiveData<Adventure> get() = _adventure

    fun fetchInProgressAdventure() {
        adventureRepository.fetchAdventuresByStatus(AdventureStatus.IN_PROGRESS) { result ->
            result
                .onSuccess { fetchAdventure(it) }
                .onFailure { }
        }
    }

    private fun fetchAdventure(adventures: List<Adventure>) {
        val dummyAdventure = Adventure(
            0,
            Destination(0, Coordinate(0.0, 0.0), ""),
            AdventureStatus.NONE,
        )

        if (adventures.isNotEmpty()) {
            _adventure.value = adventures.first()
        } else {
            _adventure.value = dummyAdventure
        }
    }
}
