package com.now.naaga.presentation.login

import androidx.lifecycle.ViewModel
import com.kakao.sdk.auth.TokenManagerProvider
import com.now.domain.model.AuthPlatformType.KAKAO
import com.now.domain.model.PlatformAuth
import com.now.domain.repository.AuthRepository
import com.now.naaga.data.local.AuthPreference

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val authPreference: AuthPreference,
) : ViewModel() {
    fun fetchToken() {
        val token = TokenManagerProvider.instance.manager.getToken()?.accessToken
        token?.let { PlatformAuth(it, KAKAO) }?.let { platformAuth ->
            authRepository.getToken(
                platformAuth,
                callback = { result ->
                    result
                        .onSuccess { authPreference.setAccessToken(it.accessToken) }
                        .onFailure { }
                },
            )
        }
    }
}
