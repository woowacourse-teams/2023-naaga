package com.now.naaga.data.repository

import com.now.domain.model.PlatformAuth
import com.now.domain.repository.AuthRepository
import com.now.naaga.NaagaApplication
import com.now.naaga.data.mapper.toDto
import com.now.naaga.data.remote.retrofit.ServicePool
import com.now.naaga.data.remote.retrofit.fetchNaagaResponse

class DefaultAuthRepository : AuthRepository {
    override fun getToken(
        platformAuth: PlatformAuth,
        callback: (Result<Boolean>) -> Unit,
    ) {
        val call = ServicePool.authService.requestToken(platformAuth.toDto())
        call.fetchNaagaResponse(
            onSuccess = { naagaAuthDto ->
                NaagaApplication.authDataSource.setAccessToken(naagaAuthDto.accessToken)
                NaagaApplication.authDataSource.setRefreshToken(naagaAuthDto.refreshToken)
                callback(Result.success(true))
            },
            onFailure = { callback(Result.failure(it)) },
        )
    }
}
