package com.example.pickapic.feature.favorites.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PictureRoomEntity::class], version = 1)
abstract class FavoritePictureDatabase : RoomDatabase() {
    abstract fun imageDao(): PictureDao
}
