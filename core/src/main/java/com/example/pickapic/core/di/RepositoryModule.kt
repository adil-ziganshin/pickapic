package com.example.pickapic.core.di

import com.example.pickapic.core.data.TopicsRepositoryImpl
import com.example.pickapic.core.domain.TopicsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTopicsRepository(
        impl: TopicsRepositoryImpl
    ): TopicsRepository
}
