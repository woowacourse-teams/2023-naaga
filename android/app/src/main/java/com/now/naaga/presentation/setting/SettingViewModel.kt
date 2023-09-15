package com.now.naaga.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.now.domain.repository.AuthRepository
import com.now.naaga.NaagaApplication.DependencyContainer.authDataSource
import com.now.naaga.data.repository.DefaultAuthRepository

class SettingViewModel(private val authRepository: AuthRepository) : ViewModel() {

    companion object {
        val Factory = SettingFactory(DefaultAuthRepository(authDataSource))

        class SettingFactory(private val authRepository: AuthRepository) :
            ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingViewModel(authRepository) as T
            }
        }
    }
}
