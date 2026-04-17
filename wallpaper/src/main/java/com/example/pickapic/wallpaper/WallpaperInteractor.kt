package com.example.pickapic.wallpaper

interface WallpaperInteractor {

    suspend fun setWallpaper(pictureUrl: String): Result<Unit>
}