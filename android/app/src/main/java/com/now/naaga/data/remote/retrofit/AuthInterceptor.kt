package com.now.naaga.data.remote.retrofit

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.now.naaga.BuildConfig
import com.now.naaga.data.remote.dto.FailureDto
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class AuthInterceptor : Interceptor {
    private val gson = Gson()
    private val client = OkHttpClient.Builder().build()

    override fun intercept(chain: Interceptor.Chain): Response {
        val headerAddedRequest = chain.request().newBuilder().addHeader("Authorization", getAccessToken()).build()
        val response = chain.proceed(headerAddedRequest)

        if (response.code == 401) {
            val failureDto = response.getDto<FailureDto>()
            when (failureDto.code) {
                101 -> throw IllegalStateException("${headerAddedRequest.url}에서 인증 정보 오류. 재로그인 필요")
                102 -> return reRequest(chain)
                103 -> throw IllegalStateException("리프레시 토큰 만료. 재로그인 필요")
            }
        }
        return response
    }

    private fun reRequest(chain: Interceptor.Chain): Response {
        val token: String = getRefreshedToken()
        val request = chain.request().newBuilder().addHeader("Authorization", token).build()
        return chain.proceed(request)
    }

    private fun getRefreshedToken(): String {
        val body = JSONObject()
            .put("refreshToken", getRefreshToken())
            .toString()
            .toRequestBody(contentType = "application/json".toMediaType())

        val request = Request.Builder()
            .url(BuildConfig.BASE_URL + "auth/refresh")
            .post(body)
            .addHeader("Authorization", getAccessToken())
            .build()

        return ""
    }

    private fun enqueueRefreshRequest(
        request: Request,
        onSuccess: (token: String) -> Unit,
        onFailure: (t: Throwable) -> Unit,
    ) {
        client.newCall(request).enqueue(
            object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    if (response.isSuccessful) {
                        // val token = response.getDto<NaagaAuthDto>()
                        // onSuccess()
                    }
                }

                override fun onFailure(call: Call, e: IOException) {
                    TODO("Not yet implemented")
                }
            },
        )
    }

    private inline fun <reified T> Response.getDto(): T {
        val responseObject = JsonParser.parseString(body?.string()).asJsonObject
        return gson.fromJson(responseObject, T::class.java)
    }

    private fun getAccessToken(): String {
        return ""
    }

    private fun getRefreshToken(): String {
        return ""
    }
}
