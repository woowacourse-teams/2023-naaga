package com.now.naaga.di

import com.now.domain.repository.AdventureRepository
import com.now.domain.repository.AuthRepository
import com.now.domain.repository.PlaceRepository
import com.now.domain.repository.RankRepository
import com.now.domain.repository.StatisticsRepository
import com.now.naaga.data.local.AuthDataSource
import com.now.naaga.data.repository.DefaultAdventureRepository
import com.now.naaga.data.repository.DefaultAuthRepository
import com.now.naaga.data.repository.DefaultPlaceRepository
import com.now.naaga.data.repository.DefaultRankRepository
import com.now.naaga.data.repository.DefaultStatisticsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Singleton
    @Provides
    fun provideRankRepository(): RankRepository = DefaultRankRepository()

    @Singleton
    @Provides
    fun provideAuthRepository(authDataSource: AuthDataSource): AuthRepository = DefaultAuthRepository(authDataSource)

    @Singleton
    @Provides
    fun provideAdventureRepository(): AdventureRepository = DefaultAdventureRepository()

    @Singleton
    @Provides
    fun providePlaceRepository(): PlaceRepository = DefaultPlaceRepository()

    @Singleton
    @Provides
    fun provideStatisticsRepository(): StatisticsRepository = DefaultStatisticsRepository()
}
