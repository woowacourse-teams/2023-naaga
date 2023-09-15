package com.now.naaga.presentation.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.now.domain.model.Adventure
import com.now.domain.model.AdventureStatus
import com.now.domain.repository.AdventureRepository
import com.now.naaga.data.repository.DefaultAdventureRepository
import kotlinx.coroutines.launch

class SplashViewModel(private val adventureRepository: AdventureRepository) : ViewModel() {
    private val _adventure = MutableLiveData<Adventure>()
    val adventure: LiveData<Adventure> = _adventure

    private val _adventureStatus = MutableLiveData<AdventureStatus>()
    val adventureStatus: LiveData<AdventureStatus> = _adventureStatus

    fun fetchInProgressAdventure() {
        viewModelScope.launch {
            runCatching {
                adventureRepository.fetchAdventureByStatus(AdventureStatus.IN_PROGRESS)
            }.onSuccess {
                fetchAdventure(it)
            }.onFailure {
                _adventureStatus.value = AdventureStatus.NONE
            }
        }
    }

    private fun fetchAdventure(adventures: List<Adventure>) {
        if (adventures.isNotEmpty()) {
            _adventure.value = adventures.first()
            _adventureStatus.value = adventures.first().adventureStatus
        } else {
            _adventureStatus.value = AdventureStatus.NONE
        }
    }

    companion object {
        val Factory = ViewModelFactory(DefaultAdventureRepository())

        class ViewModelFactory(private val adventureRepository: AdventureRepository) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SplashViewModel(adventureRepository) as T
            }
        }
    }
}
