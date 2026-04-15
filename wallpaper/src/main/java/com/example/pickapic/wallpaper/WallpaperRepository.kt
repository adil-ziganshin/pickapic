package com.example.pickapic.wallpaper

import android.app.WallpaperManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class WallpaperRepository {

    suspend fun setHomeScreenWallpaperFromUrl(
        context: Context,
        imageUrl: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val connection = (URL(imageUrl).openConnection() as HttpURLConnection).apply {
                connectTimeout = 15_000
                readTimeout = 15_000
                connect()
            }
            try {
                connection.inputStream.use { stream ->
                    val bitmap = BitmapFactory.decodeStream(stream)
                        ?: error("Could not decode image")
                    val wm = WallpaperManager.getInstance(context.applicationContext)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        wm.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM)
                    } else {
                        @Suppress("DEPRECATION")
                        wm.setBitmap(bitmap)
                    }
                    bitmap.recycle()
                }
            } finally {
                connection.disconnect()
            }
        }
    }
}
