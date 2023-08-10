package com.now.naaga.data.remote.retrofit

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.now.naaga.BuildConfig
import com.now.naaga.data.local.KakaoAuthDataSource
import com.now.naaga.presentation.login.NaagaApplication.Companion.getContext
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object RetrofitFactory {
    private const val BASE_URL = BuildConfig.BASE_URL

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .client(createOkHttpClient())
        .build()

    private fun createInterceptor(): Interceptor = Interceptor { chain ->
        val token = KakaoAuthDataSource(getContext()).getAccessToken()
        with(chain) {
            val newRequest = request().newBuilder()
                .addHeader("Authorization", token)
                .addHeader("Content-Type", "application/json")
                .build()
            proceed(newRequest)
        }
    }

    private fun createOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            addInterceptor(createInterceptor())
        }.build()
    }
}
