package com.now.naaga.data.repository

import com.now.domain.model.NaagaAuth
import com.now.domain.model.PlatformAuth
import com.now.domain.repository.AuthRepository
import com.now.naaga.data.mapper.toDomain
import com.now.naaga.data.mapper.toDto
import com.now.naaga.data.remote.retrofit.ServicePool
import com.now.naaga.data.remote.retrofit.fetchNaagaResponse

class DefaultAuthRepository : AuthRepository {
    override fun getToken(
        platformAuth: PlatformAuth,
        callback: (Result<NaagaAuth>) -> Unit,
    ) {
        val call = ServicePool.authService.requestToken(platformAuth.toDto())
        call.fetchNaagaResponse(
            onSuccess = { callback(Result.success(it.toDomain())) },
            onFailure = { callback(Result.failure(it)) },
        )
    }
}
