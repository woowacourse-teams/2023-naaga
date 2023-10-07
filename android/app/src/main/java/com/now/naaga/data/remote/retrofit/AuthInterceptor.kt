package com.now.naaga.data.remote.retrofit

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.now.naaga.BuildConfig
import com.now.naaga.NaagaApplication
import com.now.naaga.data.remote.dto.FailureDto
import com.now.naaga.data.remote.dto.NaagaAuthDto
import com.now.naaga.data.throwable.DataThrowable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject

class AuthInterceptor : Interceptor {
    private val gson = Gson()
    private val client = OkHttpClient.Builder().build()

    override fun intercept(chain: Interceptor.Chain): Response {
        val accessToken = getAccessToken() ?: return chain.proceed(chain.request())

        val headerAddedRequest = chain.request().newBuilder().addHeader(AUTH_KEY, accessToken).build()
        val response: Response = chain.proceed(headerAddedRequest)

        if (response.code == 401) {
            val newAccessToken = getRefreshedToken(accessToken).getOrElse { return response }
            return chain.proceed(chain.request().newBuilder().addHeader(AUTH_KEY, newAccessToken).build())
        }
        return response
    }

    private fun getRefreshedToken(accessToken: String): Result<String> {
        val requestBody = createRefreshRequestBody()
        val request = createRefreshRequest(requestBody, accessToken)

        val auth: NaagaAuthDto = requestRefresh(request).getOrElse {
            return Result.failure(it)
        }
        storeToken(auth.accessToken, auth.refreshToken)
        return Result.success(auth.accessToken)
    }

    private fun createRefreshRequestBody(): RequestBody {
        return JSONObject()
            .put(AUTH_REFRESH_KEY, getRefreshToken())
            .toString()
            .toRequestBody(contentType = "application/json".toMediaType())
    }

    private fun createRefreshRequest(requestBody: RequestBody, accessToken: String): Request {
        return Request.Builder()
            .url(BuildConfig.BASE_URL + AUTH_REFRESH_PATH)
            .post(requestBody)
            .addHeader(AUTH_KEY, accessToken)
            .build()
    }

    private fun requestRefresh(request: Request): Result<NaagaAuthDto> {
        val response: Response = runBlocking {
            withContext(Dispatchers.IO) { client.newCall(request).execute() }
        }
        if (response.isSuccessful) {
            return Result.success(response.getDto<NaagaAuthDto>())
        }
        val failedResponse = response.getDto<FailureDto>()
        if (failedResponse.code == 101) {
            return Result.failure(DataThrowable.AuthorizationThrowable(failedResponse.code, failedResponse.message))
        }
        return Result.failure(IllegalStateException(REFRESH_FAILURE))
    }

    private fun getAccessToken(): String? {
        return NaagaApplication.authDataSource.getAccessToken()
    }

    private fun getRefreshToken(): String {
        return requireNotNull(NaagaApplication.authDataSource.getRefreshToken()) { NO_REFRESH_TOKEN }
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

        const val NO_REFRESH_TOKEN = "리프레시 토큰이 없습니다"
        const val REFRESH_FAILURE = "토큰 리프레시 실패"
    }
}
