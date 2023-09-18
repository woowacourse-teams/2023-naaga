package com.now.naaga.presentation.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.now.domain.repository.RankRepository
import com.now.naaga.data.repository.DefaultRankRepository
import kotlinx.coroutines.launch

class SplashViewModel(private val rankRepository: RankRepository) : ViewModel() {

    private val _isTokenValid = MutableLiveData<Boolean>()
    val isTokenValid: LiveData<Boolean> = _isTokenValid

    fun getMyRank() {
        viewModelScope.launch {
            runCatching {
                rankRepository.getMyRank()
            }.onSuccess {
                _isTokenValid.value = true
            }
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
