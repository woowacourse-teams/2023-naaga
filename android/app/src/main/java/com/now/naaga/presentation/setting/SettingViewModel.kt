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
import kotlinx.coroutines.launch

class SettingViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _withdrawalStatus = MutableLiveData<Boolean>()
    val withdrawalStatus: LiveData<Boolean> = _withdrawalStatus

    private val _errorMessage = MutableLiveData<DataThrowable>()
    val errorMessage: LiveData<DataThrowable> = _errorMessage

    fun withdrawalMember() {
        viewModelScope.launch {
            runCatching { authRepository.withdrawalMember() }
                .onSuccess { _withdrawalStatus.value = it }
                .onFailure { _errorMessage.value = it as DataThrowable }
        }
    }

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
