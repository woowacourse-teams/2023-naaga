package com.now.naaga.data.repository

import com.now.domain.model.PlatformAuth
import com.now.domain.repository.AuthRepository
import com.now.naaga.data.local.AuthDataSource
import com.now.naaga.data.mapper.toDto
import com.now.naaga.data.remote.dto.RefreshTokenDto
import com.now.naaga.data.remote.retrofit.ServicePool
import com.now.naaga.data.remote.retrofit.fetchResponse
import com.now.naaga.data.throwable.DataThrowable

class DefaultAuthRepository(private val authDataSource: AuthDataSource) : AuthRepository {
    override fun getToken(
        platformAuth: PlatformAuth,
        callback: (Result<Boolean>) -> Unit,
    ) {
        val call = ServicePool.authService.requestAuth(platformAuth.toDto())
        call.fetchResponse(
            onSuccess = { naagaAuthDto ->
                authDataSource.setAccessToken(naagaAuthDto.accessToken)
                authDataSource.setRefreshToken(naagaAuthDto.refreshToken)
                callback(Result.success(true))
            },
            onFailure = { callback(Result.failure(it)) },
        )
    }

    override fun refreshToken(callback: (Result<Boolean>) -> Unit) {
        val refreshToken: String = authDataSource.getRefreshToken()
            ?: return callback(Result.failure(DataThrowable.AuthorizationThrowable(101, "로컬에 리프레시 토큰이 없습니다.")))
        val call = ServicePool.authService.refreshAuth(RefreshTokenDto(refreshToken))
        call.fetchResponse(
            onSuccess = { naagaAuthDto ->
                authDataSource.setAccessToken(naagaAuthDto.accessToken)
                authDataSource.setRefreshToken(naagaAuthDto.refreshToken)
                callback(Result.success(true))
            },
            onFailure = { callback(Result.failure(it)) },
        )
    }
}
