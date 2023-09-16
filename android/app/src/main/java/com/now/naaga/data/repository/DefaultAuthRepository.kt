package com.now.naaga.data.repository

import android.util.Log
import com.now.domain.model.PlatformAuth
import com.now.domain.repository.AuthRepository
import com.now.naaga.data.local.AuthDataSource
import com.now.naaga.data.mapper.toDto
import com.now.naaga.data.remote.retrofit.ServicePool.authService
import com.now.naaga.util.KAKAO_LOGIN_LOG_TAG
import com.now.naaga.util.getValueOrThrow
import com.now.naaga.util.unlinkWithKakao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultAuthRepository(
    private val authDataSource: AuthDataSource,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : AuthRepository {

    override suspend fun getToken(platformAuth: PlatformAuth): Boolean {
        return withContext(dispatcher) {
            val response = authService.requestAuth(platformAuth.toDto())
            runCatching {
                val naagaAuthDto = response.getValueOrThrow()
                authDataSource.setAccessToken(naagaAuthDto.accessToken)
                authDataSource.setRefreshToken(naagaAuthDto.refreshToken)
                return@withContext true
            }
            return@withContext false
        }
    }

    override suspend fun withdrawalMember(): Boolean {
        return withContext(dispatcher) {
            runCatching {
                authService.withdrawalMember()
                return@withContext true
            }.onSuccess {
                unlinkWithKakao()
            }.onFailure {
                Log.d(KAKAO_LOGIN_LOG_TAG, "${it.message}, ${it}")
            }
            return@withContext false
        }
    }
}
