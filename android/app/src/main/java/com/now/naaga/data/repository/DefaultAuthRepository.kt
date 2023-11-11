package com.now.naaga.data.repository

import com.now.domain.model.PlatformAuth
import com.now.domain.repository.AuthRepository
import com.now.naaga.data.local.AuthDataSource
import com.now.naaga.data.mapper.toDto
import com.now.naaga.data.remote.dto.post.RefreshTokenDto
import com.now.naaga.data.remote.retrofit.service.AuthService
import com.now.naaga.util.extension.getValueOrThrow
import com.now.naaga.util.unlinkWithKakao

class DefaultAuthRepository(
    private val authDataSource: AuthDataSource,
    private val authService: AuthService,
) : AuthRepository {

    override suspend fun logIn(platformAuth: PlatformAuth): Boolean {
        val response = authService.requestAuth(platformAuth.toDto())
        val naagaAuthDto = response.getValueOrThrow()
        storeToken(naagaAuthDto.accessToken, naagaAuthDto.refreshToken)
        return true
    }

    override suspend fun logout() {
        val response = authService.requestLogout(getAccessToken()!!)
        authDataSource.resetToken()
        response.getValueOrThrow()
    }

    override suspend fun withdrawalMember() {
        authService.withdrawalMember(getAccessToken()!!)
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
        val response = authService.requestRefresh(RefreshTokenDto(getRefreshToken()))
        val naagaAuthDto = response.getValueOrThrow()
        storeToken(naagaAuthDto.accessToken, naagaAuthDto.refreshToken)
    }

    companion object {
        private const val NO_REFRESH_TOKEN = "리프레시 토큰이 없습니다"
    }
}
