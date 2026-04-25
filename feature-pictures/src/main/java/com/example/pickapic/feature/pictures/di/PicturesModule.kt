package com.example.pickapic.feature.pictures.di

import com.example.pickapic.feature.pictures.data.GetPicturesUseCaseImpl
import com.example.pickapic.feature.pictures.data.PictureRepository
import com.example.pickapic.feature.pictures.data.PictureRepositoryImpl
import com.example.pickapic.feature.pictures.data.PictureService
import com.example.pickapic.feature.pictures.domain.GetPicturesUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PicturesModule {

    @Binds
    @Singleton
    abstract fun bindPictureRepository(
        impl: PictureRepositoryImpl,
    ): PictureRepository

    @Binds
    @Singleton
    abstract fun bindGetPicturesUseCase(
        impl: GetPicturesUseCaseImpl,
    ): GetPicturesUseCase
}

@Module
@InstallIn(SingletonComponent::class)
object PicturesNetworkModule {

    @Provides
    @Singleton
    fun providePictureService(retrofit: Retrofit): PictureService =
        retrofit.create(PictureService::class.java)
}