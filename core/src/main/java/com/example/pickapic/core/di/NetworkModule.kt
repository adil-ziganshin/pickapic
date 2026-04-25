package com.example.pickapic.core.di

import com.example.pickapic.core.data.PictureService
import com.example.pickapic.core.data.TopicService
import com.example.pickapic.core.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun providePictureService(retrofit: Retrofit): PictureService =
        retrofit.create(PictureService::class.java)

    @Provides
    @Singleton
    fun provideTopicService(retrofit: Retrofit): TopicService =
        retrofit.create(TopicService::class.java)
}
