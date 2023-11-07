package com.now.naaga.data.repository

import com.now.domain.model.PlatformAuth
import com.now.domain.repository.AuthRepository
import com.now.naaga.data.local.AuthDataSource
import com.now.naaga.data.mapper.toDto
import com.now.naaga.data.remote.dto.post.RefreshTokenDto
import com.now.naaga.data.remote.retrofit.service.AuthService
import com.now.naaga.util.extension.getValueOrThrow
import com.now.naaga.util.unlinkWithKakao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DefaultAuthRepository(
    private val authDataSource: AuthDataSource,
    private val authService: AuthService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : AuthRepository {

    override suspend fun logIn(platformAuth: PlatformAuth): Boolean {
        return withContext(dispatcher) {
            val response = authService.requestAuth(platformAuth.toDto())
            runCatching {
                val naagaAuthDto = response.getValueOrThrow()
                storeToken(naagaAuthDto.accessToken, naagaAuthDto.refreshToken)
                return@withContext true
            }
            return@withContext false
        }
    }

    override suspend fun logout() {
        withContext(dispatcher) {
            val response = authService.requestLogout(authDataSource.getAccessToken()!!)
            authDataSource.resetToken()
            response.getValueOrThrow()
        }
    }

    override suspend fun withdrawalMember() {
        authService.withdrawalMember(authDataSource.getAccessToken()!!)
        unlinkWithKakao()
    }

    override fun getAccessToken(): String? {
        return authDataSource.getAccessToken()
    }

    override fun getRefreshToken(): String {
        return requireNotNull(authDataSource.getRefreshToken()) { NO_REFRESH_TOKEN }
    }

    override fun storeToken(accessToken: String, refreshToken: String) {
        authDataSource.setAccessToken(accessToken)
        authDataSource.setRefreshToken(refreshToken)
    }

    override suspend fun refreshAccessToken() {
        val response = authService.requestRefresh(RefreshTokenDto(authDataSource.getRefreshToken()!!))
        val naagaAuthDto = response.getValueOrThrow()
        storeToken(naagaAuthDto.accessToken, naagaAuthDto.refreshToken)
    }

    companion object {
        private const val NO_REFRESH_TOKEN = "리프레시 토큰이 없습니다"
    }
}
