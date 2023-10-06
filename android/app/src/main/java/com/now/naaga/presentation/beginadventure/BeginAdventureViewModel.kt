package com.now.naaga.presentation.beginadventure

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.now.domain.model.Adventure
import com.now.domain.model.AdventureStatus
import com.now.domain.repository.AdventureRepository
import com.now.naaga.data.throwable.DataThrowable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BeginAdventureViewModel @Inject constructor(private val adventureRepository: AdventureRepository) : ViewModel() {
    private val _adventure = MutableLiveData<Adventure>()
    val adventure: LiveData<Adventure> = _adventure

    private val _error = MutableLiveData<DataThrowable>()
    val error: LiveData<DataThrowable> = _error

    private val _loading = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean> = _loading

    fun fetchAdventure(adventureStatus: AdventureStatus) {
        _loading.value = true
        viewModelScope.launch {
            runCatching {
                adventureRepository.fetchAdventureByStatus(adventureStatus)
            }.onSuccess {
                _loading.value = false
                _adventure.value = it.firstOrNull()
            }.onFailure {
                _error.value = it as DataThrowable
            }
        }
    }
}
