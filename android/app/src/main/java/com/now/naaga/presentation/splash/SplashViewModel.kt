package com.now.naaga.presentation.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.now.domain.repository.AuthRepository
import com.now.naaga.NaagaApplication
import com.now.naaga.data.repository.DefaultAuthRepository
import com.now.naaga.data.throwable.DataThrowable

class SplashViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val _event = MutableLiveData<Event>()
    val event: LiveData<Event> get() = _event

    fun refreshAuth() {
        authRepository.refreshToken { result ->
            result.onSuccess {
                _event.value = Event.RefreshSucceed
            }.onFailure {
                when (it as DataThrowable) {
                    is DataThrowable.AuthorizationThrowable -> _event.value = Event.RefreshFailed
                    else -> throw it
                }
            }
        }
    }

    sealed interface Event {
        object RefreshSucceed : Event
        object RefreshFailed : Event
    }

    companion object {
        val Factory = ViewModelFactory(DefaultAuthRepository(NaagaApplication.authDataSource))

        class ViewModelFactory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SplashViewModel(authRepository) as T
            }
        }
    }
}
