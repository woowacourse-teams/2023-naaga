package com.now.naaga.data.repository

import android.util.Log
import com.now.domain.model.PlatformAuth
import com.now.domain.repository.AuthRepository
import com.now.naaga.data.local.AuthDataSource
import com.now.naaga.data.mapper.toDto
import com.now.naaga.data.remote.retrofit.ServicePool.authService
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
                Log.d("test", "회원탈퇴 성공")
                return@withContext true
            }.onSuccess {
                unlinkWithKakao()
                Log.d("test", "카카오 회원탈퇴 성공")
            }.onFailure {
                Log.d("test", "${it.message}, ${it}")
            }
            Log.d("test", "회원탈퇴 실패")
            return@withContext false
        }
    }
}
