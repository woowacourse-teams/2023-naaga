package com.now.naaga.presentation.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.now.domain.repository.StatisticsRepository
import com.now.naaga.data.repository.DefaultStatisticsRepository
import com.now.naaga.data.throwable.DataThrowable
import kotlinx.coroutines.launch

class SplashViewModel(private val statisticsRepository: StatisticsRepository) : ViewModel() {

    private val _isTokenValid = MutableLiveData<Boolean>()
    val isTokenValid: LiveData<Boolean> = _isTokenValid

    private val _error = MutableLiveData<DataThrowable>()
    val error: LiveData<DataThrowable> = _error

    fun testTokenValid() {
        viewModelScope.launch {
            runCatching {
                statisticsRepository.getMyStatistics()
            }.onSuccess {
                _isTokenValid.value = true
            }.onFailure {
                _isTokenValid.value = false
                _error.value = it as DataThrowable.AuthorizationThrowable
            }
        }
    }

    companion object {
        val Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SplashViewModel(DefaultStatisticsRepository()) as T
            }
        }
    }
}
