package com.now.naaga.presentation.onadventure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.now.domain.repository.AdventureRepository

class OnAdventureFactory(private val adventureRepository: AdventureRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OnAdventureViewModel::class.java)) {
            return OnAdventureViewModel(adventureRepository) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}