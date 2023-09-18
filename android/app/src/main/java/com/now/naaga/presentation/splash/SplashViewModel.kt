package com.now.naaga.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.now.domain.repository.RankRepository
import com.now.naaga.data.repository.DefaultRankRepository
import kotlinx.coroutines.launch

class SplashViewModel(private val rankRepository: RankRepository) : ViewModel() {

    fun getMyRank() {
        viewModelScope.launch {
            rankRepository.getMyRank()
        }
    }

    companion object {
        val Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SplashViewModel(DefaultRankRepository()) as T
            }
        }
    }
}
