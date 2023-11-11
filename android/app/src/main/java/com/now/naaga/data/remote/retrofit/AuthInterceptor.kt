package com.now.naaga.data.remote.retrofit

import com.now.domain.repository.AuthRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.closeQuietly

class AuthInterceptor(
    private val authRepository: AuthRepository,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = authRepository.getAccessToken() ?: return chain.proceed(chain.request())

        val tokenAddedRequest = chain.request().putToken(accessToken)
        val response: Response = chain.proceed(tokenAddedRequest)

        if (response.isTokenInvalid()) {
            response.closeQuietly()
            runCatching {
                runBlocking { authRepository.refreshAccessToken() }
            }
            return chain.proceed(chain.request().putToken(authRepository.getAccessToken()!!))
        }
        return response
    }

    private fun Response.isTokenInvalid(): Boolean {
        return this.code == 401
    }

    private fun Request.putToken(accessToken: String): Request {
        return this.newBuilder()
            .addHeader(AUTH_KEY, accessToken)
            .build()
    }

    companion object {
        private const val AUTH_KEY = "Authorization"
    }
}
