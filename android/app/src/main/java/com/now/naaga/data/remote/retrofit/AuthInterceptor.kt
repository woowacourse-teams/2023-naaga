package com.now.naaga.data.remote.retrofit

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.now.naaga.BuildConfig
import com.now.naaga.NaagaApplication
import com.now.naaga.data.remote.dto.FailureDto
import com.now.naaga.data.remote.dto.NaagaAuthDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject

class AuthInterceptor : Interceptor {
    private val gson = Gson()
    private val client = OkHttpClient.Builder().build()

    override fun intercept(chain: Interceptor.Chain): Response {
        if (isLoginRequest(chain.request())) {
            return chain.proceed(chain.request())
        }

        val headerAddedRequest = chain.request().newBuilder().addHeader(AUTH_KEY, getAccessToken()).build()
        val response = chain.proceed(headerAddedRequest)

        if (response.code == 401) {
            val failureDto = response.getDto<FailureDto>()
            when (failureDto.code) {
                101 -> throw IllegalStateException(AUTH_ERROR.format(headerAddedRequest.url.toString()))
                102 -> {
                    response.close()
                    return reRequest(chain)
                }

                103 -> throw IllegalStateException(REFRESH_EXPIRED)
            }
        }
        return response
    }

    private fun isLoginRequest(request: Request): Boolean {
        val path: String = request.url.encodedPath.substringAfter(BuildConfig.BASE_URL)
        val httpMethod = request.method
        return (path.contains(AUTH_PATH)) and (httpMethod == POST)
    }

    private fun reRequest(chain: Interceptor.Chain): Response {
        val token: String = getRefreshedToken()
        val request = chain.request().newBuilder().addHeader(AUTH_KEY, token).build()
        return chain.proceed(request)
    }

    private fun getRefreshedToken(): String {
        val body = JSONObject()
            .put(AUTH_REFRESH_KEY, getRefreshToken())
            .toString()
            .toRequestBody(contentType = "application/json".toMediaType())

        val request = Request.Builder()
            .url(BuildConfig.BASE_URL + AUTH_REFRESH_PATH)
            .post(body)
            .addHeader(AUTH_KEY, getAccessToken())
            .build()

        val auth = requestRefresh(request)
        storeToken(auth.accessToken, auth.refreshToken)
        return auth.accessToken
    }

    private fun requestRefresh(request: Request): NaagaAuthDto {
        val response: Response = runBlocking {
            withContext(Dispatchers.IO) { client.newCall(request).execute() }
        }
        if (response.isSuccessful) {
            return response.getDto<NaagaAuthDto>()
        }
        throw IllegalStateException(REFRESH_FAILURE)
    }

    private fun getAccessToken(): String {
        return NaagaApplication.authDataSource.getAccessToken() ?: throw IllegalStateException(NO_ACCESS_TOKEN)
    }

    private fun getRefreshToken(): String {
        return NaagaApplication.authDataSource.getRefreshToken() ?: throw IllegalStateException(NO_REFRESH_TOKEN)
    }

    private fun storeToken(accessToken: String, refreshToken: String) {
        NaagaApplication.authDataSource.setAccessToken(accessToken)
        NaagaApplication.authDataSource.setRefreshToken(refreshToken)
    }

    private inline fun <reified T> Response.getDto(): T {
        val responseObject = JsonParser.parseString(body?.string()).asJsonObject
        return gson.fromJson(responseObject, T::class.java)
    }

    companion object {
        const val AUTH_KEY = "Authorization"
        const val AUTH_REFRESH_KEY = "refreshToken"

        const val AUTH_PATH = "auth"
        const val AUTH_REFRESH_PATH = "auth/refresh"

        const val POST = "POST"

        const val AUTH_ERROR = "%s에서 인증 오류 발생"
        const val REFRESH_EXPIRED = "리프레시 토큰 만료"
        const val NO_ACCESS_TOKEN = "엑세스 토큰이 없습니다"
        const val NO_REFRESH_TOKEN = "리프레시 토큰이 없습니다"
        const val REFRESH_FAILURE = "토큰 리프레시 실패"
    }
}
