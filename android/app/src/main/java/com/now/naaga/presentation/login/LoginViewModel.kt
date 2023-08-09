package com.now.naaga.presentation.login

import androidx.lifecycle.ViewModel
import com.now.domain.model.AuthPlatformType.KAKAO
import com.now.domain.model.PlatformAuth
import com.now.domain.repository.AuthRepository
import com.now.naaga.data.local.AuthDataSource

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val authDataSource: AuthDataSource,
) : ViewModel() {
    fun fetchToken(token: String) {
        authRepository.getToken(
            PlatformAuth(token, KAKAO),
            callback = { result ->
                result
                    .onSuccess { authDataSource.setAccessToken(it.accessToken) }
                    .onFailure { }
            },
        )
    }
}
