package com.now.naaga.presentation.adventureresult

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.now.domain.repository.AdventureRepository

class AdventureResultFactory(private val adventureRepository: AdventureRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdventureResultViewModel::class.java)) {
            return AdventureResultViewModel(adventureRepository) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
