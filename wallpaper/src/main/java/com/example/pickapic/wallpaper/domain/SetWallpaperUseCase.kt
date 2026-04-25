package com.example.pickapic.wallpaper.domain

interface SetWallpaperUseCase {

    suspend fun setWallpaper(pictureUrl: String): Result<Unit>
}