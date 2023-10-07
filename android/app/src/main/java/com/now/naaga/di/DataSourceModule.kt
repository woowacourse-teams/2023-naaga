package com.now.naaga.di

import android.content.Context
import com.now.naaga.data.local.AuthDataSource
import com.now.naaga.data.local.DefaultAuthDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DataSourceModule {
    @Singleton
    @Provides
    fun provideAuthDatasource(@ApplicationContext context: Context): AuthDataSource = DefaultAuthDataSource(context)
}
