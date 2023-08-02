package com.now.naaga.presentation.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.now.domain.model.AdventureStatus
import com.now.domain.model.Game
import com.now.domain.repository.AdventureRepository2
import com.now.naaga.data.repository.ThirdDemoAdventureRepository

class SplashViewModel(private val adventureRepository2: AdventureRepository2) : ViewModel() {
    private val _adventure = MutableLiveData<Game>()
    val adventure: LiveData<Game> get() = _adventure

    private val _adventureStatus = MutableLiveData<AdventureStatus>()
    val adventureStatus: LiveData<AdventureStatus> = _adventureStatus

    fun fetchInProgressAdventure() {
        adventureRepository2.fetchAdventureByStatus(AdventureStatus.IN_PROGRESS) { result: Result<List<Game>> ->
            result
                .onSuccess { fetchAdventure(it) }
                .onFailure { _adventureStatus.value = AdventureStatus.NONE }
        }
    }

    private fun fetchAdventure(adventures: List<Game>) {
        if (adventures.isNotEmpty()) {
            _adventure.value = adventures.first()
            _adventureStatus.value = adventures.first().adventureStatus
        } else {
            _adventureStatus.value = AdventureStatus.NONE
        }
    }
}
