package com.example.pickapic.wallpaper

import android.app.WallpaperManager
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL
import javax.inject.Inject


class WallpaperInteractorImpl @Inject constructor(
    private val wallpaperManager: WallpaperManager
): WallpaperInteractor {

    override suspend fun setWallpaper(
        pictureUrl: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val inputStream = URL(pictureUrl).openStream()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                wallpaperManager.setStream(
                    inputStream,
                    null,
                    true,
                    WallpaperManager.FLAG_SYSTEM
                )
            } else {
                wallpaperManager.setStream(inputStream)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    private companion object {
        const val TAG = "WallpaperInteractor"
    }
}