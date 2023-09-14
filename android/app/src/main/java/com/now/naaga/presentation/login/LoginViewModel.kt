package com.now.naaga.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.now.domain.model.AuthPlatformType
import com.now.domain.model.PlatformAuth
import com.now.domain.repository.AuthRepository
import com.now.naaga.data.throwable.DataThrowable
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _isLoginSucceed = MutableLiveData<Boolean>()
    val isLoginSucceed: LiveData<Boolean> = _isLoginSucceed

    private val _throwable = MutableLiveData<DataThrowable>()
    val throwable: LiveData<DataThrowable> = _throwable

    fun signIn(token: String, platformType: AuthPlatformType) {
        viewModelScope.launch {
            runCatching {
                authRepository.getToken(PlatformAuth(token, platformType))
            }.onSuccess { status ->
                _isLoginSucceed.value = status
            }.onFailure {
                _throwable.value = it as DataThrowable
            }
        }
    }
}
