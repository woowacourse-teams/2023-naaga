package com.now.naaga.presentation.adventurehistory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.now.domain.model.AdventureResult
import com.now.domain.model.OrderType
import com.now.domain.model.SortType
import com.now.domain.repository.AdventureRepository
import com.now.naaga.data.repository.DefaultAdventureRepository

class AdventureHistoryViewModel(private val adventureRepository: AdventureRepository) : ViewModel() {
    private val _adventureResults = MutableLiveData<List<AdventureResult>>()
    val adventureResults: LiveData<List<AdventureResult>> = _adventureResults

    fun fetchHistories() {
        adventureRepository.fetchMyAdventureResults(SortType.TIME, OrderType.DESCENDING) { result ->
            result.onSuccess { _adventureResults.value = it }
        }
    }

    companion object {
        val Factory = AdventureHistoryFactory(DefaultAdventureRepository())

        class AdventureHistoryFactory(private val adventureRepository: AdventureRepository) :
            ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AdventureHistoryViewModel(adventureRepository) as T
            }
        }
    }
}
