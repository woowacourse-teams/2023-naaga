package com.now.naaga.data.repository

import com.now.domain.model.PlatformAuth
import com.now.domain.repository.AuthRepository
import com.now.naaga.data.local.AuthDataSource
import com.now.naaga.data.mapper.toDto
import com.now.naaga.data.remote.retrofit.ServicePool
import com.now.naaga.data.remote.retrofit.fetchResponse

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
}
