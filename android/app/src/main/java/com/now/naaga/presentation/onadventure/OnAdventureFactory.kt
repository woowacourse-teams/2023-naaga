package com.now.naaga.presentation.onadventure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.now.domain.repository.DestinationRepository

class OnAdventureFactory(private val destinationRepository: DestinationRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OnAdventureViewModel::class.java)) {
            return OnAdventureViewModel(destinationRepository) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
