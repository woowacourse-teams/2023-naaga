package com.now.naaga.presentation.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.now.domain.repository.StatisticsRepository
import com.now.naaga.data.throwable.DataThrowable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val statisticsRepository: StatisticsRepository) : ViewModel() {

    private val _isTokenValid = MutableLiveData<Boolean>()
    val isTokenValid: LiveData<Boolean> = _isTokenValid

    private val _throwable = MutableLiveData<DataThrowable>()
    val throwable: LiveData<DataThrowable> = _throwable

    fun testTokenValid() {
        viewModelScope.launch {
            runCatching {
                statisticsRepository.getMyStatistics()
            }.onSuccess {
                _isTokenValid.value = true
            }.onFailure {
                _isTokenValid.value = false
                setThrowable(it)
            }
        }
    }

    private fun setThrowable(throwable: Throwable) {
        when (throwable) {
            is IOException -> {
                if (isAuthorizationThrowable(throwable)) {
                    _throwable.value = DataThrowable.AuthorizationThrowable(EXPIRATION_AUTH_ERROR_CODE, "")
                }
            }
        }
    }

    private fun isAuthorizationThrowable(throwable: Throwable): Boolean {
        if (throwable.message == null) return false
        return throwable.message!!.contains(AUTHORIZATION_THROWABLE)
    }

    companion object {
        const val EXPIRATION_AUTH_ERROR_CODE = 102
        private const val AUTHORIZATION_THROWABLE = "AuthorizationThrowable"
    }
}
