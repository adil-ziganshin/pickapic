package com.example.pickapic.feature.favorites.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PictureDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPicture(picture: PictureRoomEntity)

    @Query("SELECT * FROM Pictures")
    fun getAllPictures(): Flow<List<PictureRoomEntity>>

    @Query("SELECT * FROM Pictures WHERE topic = :topic")
    suspend fun getPicturesByTopic(topic: String): List<PictureRoomEntity>

    @Query("DELETE FROM Pictures")
    suspend fun clearAll()
}
