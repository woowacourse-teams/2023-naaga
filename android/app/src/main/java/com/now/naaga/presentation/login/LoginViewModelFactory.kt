package com.now.naaga.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.now.domain.repository.AuthRepository
import com.now.naaga.data.local.AuthPreference

class LoginViewModelFactory(
    private val authRepository: AuthRepository,
    private val authPreference: AuthPreference,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(authRepository, authPreference) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
