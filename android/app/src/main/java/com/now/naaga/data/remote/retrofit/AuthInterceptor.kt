package com.now.naaga.data.remote.retrofit

import com.now.domain.repository.AuthRepository
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.closeQuietly

class AuthInterceptor(
    private val authRepository: AuthRepository,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = authRepository.getAccessToken() ?: return chain.proceed(chain.request())

        val headerAddedRequest = chain.request().newBuilder().addHeader(AUTH_KEY, accessToken).build()
        val response: Response = chain.proceed(headerAddedRequest)

        if (response.code == 401) {
            response.closeQuietly()
            runBlocking {
                authRepository.refreshAccessToken()
            }
            return chain.proceed(
                chain.request().newBuilder().addHeader(AUTH_KEY, authRepository.getAccessToken()!!).build(),
            )
        }
        return response
    }

    companion object {
        private const val AUTH_KEY = "Authorization"
    }
}
