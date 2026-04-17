package com.example.pickapic.wallpaper

import android.app.WallpaperManager
import android.content.Context
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
        wallpaperInteractorImpl: WallpaperInteractorImpl
    ): WallpaperInteractor

    companion object {
        @Provides
        @Singleton
        fun provideWallpaperManager(
            @ApplicationContext context: Context
        ): WallpaperManager = WallpaperManager.getInstance(context)
    }
}
