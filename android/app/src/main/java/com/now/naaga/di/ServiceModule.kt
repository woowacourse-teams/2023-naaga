package com.now.naaga.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.now.domain.repository.AuthRepository
import com.now.naaga.BuildConfig
import com.now.naaga.data.remote.retrofit.AuthInterceptor
import com.now.naaga.data.remote.retrofit.service.AdventureService
import com.now.naaga.data.remote.retrofit.service.AuthService
import com.now.naaga.data.remote.retrofit.service.LetterService
import com.now.naaga.data.remote.retrofit.service.PlaceService
import com.now.naaga.data.remote.retrofit.service.RankService
import com.now.naaga.data.remote.retrofit.service.StatisticsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class CommonRetrofit

@Module
@InstallIn(SingletonComponent::class)
class ServiceModule {
    private val BASE_URL = BuildConfig.BASE_URL

    @Singleton
    @Provides
    fun provideOkHttpClient(authRepository: AuthRepository): OkHttpClient = OkHttpClient.Builder().apply {
        addInterceptor(AuthInterceptor(authRepository))
    }.build()

    @AuthRetrofit
    @Singleton
    @Provides
    fun provideAuthRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .client(okHttpClient)
        .build()

    @CommonRetrofit
    @Singleton
    @Provides
    fun provideNormalRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Singleton
    @Provides
    fun provideRankService(@AuthRetrofit retrofit: Retrofit): RankService = retrofit.create(RankService::class.java)

    @Singleton
    @Provides
    fun provideStatisticsService(@AuthRetrofit retrofit: Retrofit): StatisticsService = retrofit.create(
        StatisticsService::class.java,
    )

    @Singleton
    @Provides
    fun provideAdventureService(@AuthRetrofit retrofit: Retrofit): AdventureService = retrofit.create(
        AdventureService::class.java,
    )

    @Singleton
    @Provides
    fun providePlaceService(@AuthRetrofit retrofit: Retrofit): PlaceService = retrofit.create(PlaceService::class.java)

    @Singleton
    @Provides
    fun provideAuthService(@CommonRetrofit retrofit: Retrofit): AuthService = retrofit.create(AuthService::class.java)

    @Singleton
    @Provides
    fun provideLetterService(@AuthRetrofit retrofit: Retrofit): LetterService =
        retrofit.create(LetterService::class.java)
}
