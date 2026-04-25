package com.example.pickapic.wallpaper.di

import android.app.WallpaperManager
import android.content.Context
import com.example.pickapic.wallpaper.data.SetWallpaperUseCaseImpl
import com.example.pickapic.wallpaper.domain.SetWallpaperUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface WallpaperModule {

    @Binds
    fun bindWallpaperInteractor(
        useCaseImpl: SetWallpaperUseCaseImpl
    ): SetWallpaperUseCase

    companion object {
        @Provides
        @Singleton
        fun provideWallpaperManager(
            @ApplicationContext context: Context
        ): WallpaperManager = WallpaperManager.getInstance(context)
    }
}