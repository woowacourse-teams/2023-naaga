package com.now.naaga.presentation.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.now.domain.repository.AuthRepository
import com.now.naaga.NaagaApplication.DependencyContainer.authDataSource
import com.now.naaga.data.repository.DefaultAuthRepository
import com.now.naaga.data.throwable.DataThrowable
import com.now.naaga.data.throwable.DataThrowable.AuthorizationThrowable
import kotlinx.coroutines.launch
import com.now.naaga.data.throwable.DataThrowable
import kotlinx.coroutines.launch

class SettingViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _isLoggedIn = MutableLiveData(true)
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    private val _throwable = MutableLiveData<DataThrowable>()
    val throwable: LiveData<DataThrowable> = _throwable

    fun logout() {
        viewModelScope.launch {
            runCatching {
                authRepository.logout()
            }.onSuccess {
                _isLoggedIn.value = false
            }.onFailure {
                setError(it as DataThrowable)
            }
        }
    }

    private fun setError(throwable: DataThrowable) {
        when (throwable) {
            is AuthorizationThrowable -> _throwable.value = throwable
            else -> {}
        }
    }

    private val _withdrawalStatus = MutableLiveData<Boolean>()
    val withdrawalStatus: LiveData<Boolean> = _withdrawalStatus

    private val _errorMessage = MutableLiveData<DataThrowable>()
    val errorMessage: LiveData<DataThrowable> = _errorMessage

    fun withdrawalMember() {
        viewModelScope.launch {
            runCatching { authRepository.withdrawalMember() }
                .onSuccess { _withdrawalStatus.value = true }
                .onFailure { _errorMessage.value = it as DataThrowable }
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
