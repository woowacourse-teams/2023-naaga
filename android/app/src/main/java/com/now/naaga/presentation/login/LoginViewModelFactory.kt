package com.now.naaga.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.now.domain.repository.AuthRepository
import com.now.naaga.data.local.AuthDataSource

class LoginViewModelFactory(
    private val authRepository: AuthRepository,
    private val authDataSource: AuthDataSource,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(authRepository, authDataSource) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
