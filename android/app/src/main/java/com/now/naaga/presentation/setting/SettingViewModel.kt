package com.now.naaga.presentation.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.now.domain.repository.AuthRepository
import com.now.naaga.NaagaApplication.DependencyContainer.authDataSource
import com.now.naaga.data.repository.DefaultAuthRepository
import kotlinx.coroutines.launch

class SettingViewModel(private val authRepository: AuthRepository) : ViewModel() {

    fun logout() {
        viewModelScope.launch {
            runCatching {
                authRepository.logout()
            }.onSuccess {
            }.onFailure {
            }
        }
    }

    companion object {
        val Factory = SettingFactory(DefaultAuthRepository(authDataSource))

        @Suppress("UNCHECKED_CAST")
        class SettingFactory(private val authRepository: AuthRepository) :
            ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingViewModel(authRepository) as T
            }
        }
    }
}
