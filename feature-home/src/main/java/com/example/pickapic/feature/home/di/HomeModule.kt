package com.example.pickapic.feature.home.di

import com.example.pickapic.feature.home.data.TopicService
import com.example.pickapic.feature.home.data.TopicsRepository
import com.example.pickapic.feature.home.data.TopicsRepositoryImpl
import com.example.pickapic.feature.home.domain.GetTopicsUseCase
import com.example.pickapic.feature.home.data.GetTopicsUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface HomeModule {

    @Binds
    @Singleton
    fun bindGetTopicsUseCase(
        impl: GetTopicsUseCaseImpl
    ): GetTopicsUseCase

    @Binds
    @Singleton
    fun bindTopicsRepository(
        impl: TopicsRepositoryImpl
    ): TopicsRepository
}

@Module
@InstallIn(SingletonComponent::class)
object HomeNetworkModule {

    @Provides
    @Singleton
    fun provideTopicService(retrofit: Retrofit): TopicService =
        retrofit.create(TopicService::class.java)
}