package com.now.naaga.presentation.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.now.domain.repository.AuthRepository
import com.now.naaga.data.throwable.DataThrowable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel() {
    private val _isLoggedIn = MutableLiveData(true)
    val isLoggedIn: LiveData<Boolean> = _isLoggedIn

    private val _throwable = MutableLiveData<DataThrowable>()
    val throwable: LiveData<DataThrowable> = _throwable

    private val _withdrawalStatus = MutableLiveData<Boolean>()
    val withdrawalStatus: LiveData<Boolean> = _withdrawalStatus

    fun logout() {
        viewModelScope.launch {
            runCatching { authRepository.logout() }
                .onSuccess { _isLoggedIn.value = false }
                .onFailure { setThrowable(it) }
        }
    }

    fun withdrawalMember() {
        viewModelScope.launch {
            runCatching { authRepository.withdrawalMember() }
                .onSuccess { _withdrawalStatus.value = true }
                .onFailure { setThrowable(it) }
        }
    }

    private fun setThrowable(throwable: Throwable) {
        when (throwable) {
            is DataThrowable.AuthorizationThrowable -> _throwable.value = throwable
        }
    }
}
