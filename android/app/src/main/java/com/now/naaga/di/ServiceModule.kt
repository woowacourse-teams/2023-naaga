package com.now.naaga.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.now.naaga.BuildConfig
import com.now.naaga.NaagaApplication
import com.now.naaga.data.remote.retrofit.service.AdventureService
import com.now.naaga.data.remote.retrofit.service.AuthService
import com.now.naaga.data.remote.retrofit.service.PlaceService
import com.now.naaga.data.remote.retrofit.service.RankService
import com.now.naaga.data.remote.retrofit.service.StatisticsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ServiceModule {
    private val BASE_URL = BuildConfig.BASE_URL

    @Singleton
    @Provides
    fun createInterceptor(): Interceptor = Interceptor { chain ->
        val token: String = NaagaApplication.authDataSource.getAccessToken() ?: ""
        with(chain) {
            val newRequest = request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .addHeader("Content-Type", "application/json")
                .build()
            proceed(newRequest)
        }
    }

    @Singleton
    @Provides
    fun createOkHttpClient(interceptor: Interceptor): OkHttpClient {
        return OkHttpClient.Builder().apply {
            addInterceptor(interceptor)
        }.build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun provideRankService(retrofit: Retrofit): RankService = retrofit.create(RankService::class.java)

    @Singleton
    @Provides
    fun provideStatisticsService(retrofit: Retrofit): StatisticsService = retrofit.create(StatisticsService::class.java)

    @Singleton
    @Provides
    fun provideAdventureService(retrofit: Retrofit): AdventureService = retrofit.create(AdventureService::class.java)

    @Singleton
    @Provides
    fun providePlaceService(retrofit: Retrofit): PlaceService = retrofit.create(PlaceService::class.java)

    @Singleton
    @Provides
    fun provideAuthService(retrofit: Retrofit): AuthService = retrofit.create(AuthService::class.java)
}
