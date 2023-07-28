package com.now.naaga.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.now.domain.repository.AdventureRepository

class SplashViewModelFactory(private val adventureRepository: AdventureRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
            return SplashViewModel(adventureRepository) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
