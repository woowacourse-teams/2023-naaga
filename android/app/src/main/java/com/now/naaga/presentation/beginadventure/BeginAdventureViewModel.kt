package com.now.naaga.presentation.beginadventure

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.now.domain.model.Adventure
import com.now.domain.model.AdventureStatus
import com.now.domain.repository.AdventureRepository
import com.now.naaga.data.repository.DefaultAdventureRepository
import com.now.naaga.data.throwable.DataThrowable

class BeginAdventureViewModel(private val adventureRepository: AdventureRepository) : ViewModel() {
    private val _adventure = MutableLiveData<Adventure>()
    val adventure: LiveData<Adventure> = _adventure

    private val _error = MutableLiveData<DataThrowable>()
    val error: LiveData<DataThrowable> = _error

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading

    fun fetchAdventure(adventureStatus: AdventureStatus) {
        _loading.value = true
        adventureRepository.fetchAdventureByStatus(adventureStatus) { result ->
            _loading.value = false
            result
                .onSuccess { _adventure.value = it.nullableFirst() }
                .onFailure { _error.value = it as DataThrowable }
        }
    }

    private fun <T> List<T>.nullableFirst(): T? {
        if (isNotEmpty()) {
            return first()
        }
        return null
    }

    companion object {
        val Factory = BeginAdventureViewModelFactory(DefaultAdventureRepository())

        class BeginAdventureViewModelFactory(
            private val adventureRepository: AdventureRepository,
        ) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return BeginAdventureViewModel(adventureRepository) as T
            }
        }
    }
}
