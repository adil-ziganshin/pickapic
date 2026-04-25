package com.example.pickapic.feature.home.di

import com.example.pickapic.feature.home.data.TopicsRepository
import com.example.pickapic.feature.home.data.TopicsRepositoryImpl
import com.example.pickapic.feature.home.domain.GetTopicsUseCase
import com.example.pickapic.feature.home.domain.GetTopicsUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeModule {

    @Binds
    @Singleton
    abstract fun bindGetTopicsUseCase(
        impl: GetTopicsUseCaseImpl
    ): GetTopicsUseCase

    @Binds
    @Singleton
    abstract fun bindTopicsRepository(
        impl: TopicsRepositoryImpl
    ): TopicsRepository
}