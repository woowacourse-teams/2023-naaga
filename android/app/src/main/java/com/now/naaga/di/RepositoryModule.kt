package com.now.naaga.di

import com.now.domain.repository.AdventureRepository
import com.now.domain.repository.AuthRepository
import com.now.domain.repository.LetterRepository
import com.now.domain.repository.PlaceRepository
import com.now.domain.repository.ProfileRepository
import com.now.domain.repository.RankRepository
import com.now.domain.repository.StatisticsRepository
import com.now.naaga.data.local.AuthDataSource
import com.now.naaga.data.remote.retrofit.service.AdventureService
import com.now.naaga.data.remote.retrofit.service.AuthService
import com.now.naaga.data.remote.retrofit.service.LetterService
import com.now.naaga.data.remote.retrofit.service.PlaceService
import com.now.naaga.data.remote.retrofit.service.ProfileService
import com.now.naaga.data.remote.retrofit.service.RankService
import com.now.naaga.data.remote.retrofit.service.StatisticsService
import com.now.naaga.data.repository.DefaultAdventureRepository
import com.now.naaga.data.repository.DefaultAuthRepository
import com.now.naaga.data.repository.DefaultLetterRepository
import com.now.naaga.data.repository.DefaultPlaceRepository
import com.now.naaga.data.repository.DefaultProfileRepository
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
    fun provideRankRepository(rankService: RankService): RankRepository = DefaultRankRepository(rankService)

    @Singleton
    @Provides
    fun provideAuthRepository(authDataSource: AuthDataSource, authService: AuthService): AuthRepository =
        DefaultAuthRepository(authDataSource, authService)

    @Singleton
    @Provides
    fun provideAdventureRepository(adventureService: AdventureService): AdventureRepository =
        DefaultAdventureRepository(adventureService)

    @Singleton
    @Provides
    fun providePlaceRepository(placeService: PlaceService): PlaceRepository = DefaultPlaceRepository(placeService)

    @Singleton
    @Provides
    fun provideStatisticsRepository(statisticsService: StatisticsService): StatisticsRepository =
        DefaultStatisticsRepository(statisticsService)

    @Singleton
    @Provides
    fun provideLetterRepository(letterService: LetterService): LetterRepository =
        DefaultLetterRepository(letterService)

    @Singleton
    @Provides
    fun provideProfileRepository(profileService: ProfileService): ProfileRepository =
        DefaultProfileRepository(profileService)
}
