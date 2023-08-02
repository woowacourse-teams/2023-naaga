package com.now.naaga.data.remote.retrofit

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

object RetrofitFactory {
    private const val BASIC_USER_TOKEN = "Basic MTExQHdvb3dhLmNvbToxMTEx"

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://154c0926-f3af-42e2-8af1-bcf8115f3457.mock.pstmn.io")
        // .baseUrl("http://43.202.67.161")
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .client(createOkHttpClient())
        .build()

    private fun createInterceptor(): Interceptor = Interceptor { chain ->
        with(chain) {
            val newRequest = request().newBuilder()
                .addHeader("Authorization", BASIC_USER_TOKEN)
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
