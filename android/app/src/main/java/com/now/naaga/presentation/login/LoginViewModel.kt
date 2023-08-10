package com.now.naaga.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.now.domain.model.AuthPlatformType
import com.now.domain.model.PlatformAuth
import com.now.domain.repository.AuthRepository

class LoginViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _isLoginSucceed = MutableLiveData<Boolean>()
    val isLoginSucceed: LiveData<Boolean> = _isLoginSucceed

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun signIn(token: String, platformType: AuthPlatformType) {
        authRepository.getToken(PlatformAuth(token, platformType)) { result ->
            result
                .onSuccess { _isLoginSucceed.value = it }
                .onFailure { _errorMessage.value = it.message }
        }
    }
}
