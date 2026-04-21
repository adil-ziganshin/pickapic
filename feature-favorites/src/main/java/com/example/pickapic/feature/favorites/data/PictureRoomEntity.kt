package com.example.pickapic.feature.favorites.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pickapic.feature.favorites.domain.FavoritePicture

@Entity(tableName = "pictures")
data class PictureRoomEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val previewUrl: String,
    val fullPicUrl: String,
    val smallUrl: String,
    val topic: String
)

fun PictureRoomEntity.toFavoritePicture(): FavoritePicture {
    return FavoritePicture(
        id = this.id,
        previewUrl = this.previewUrl,
        fullPicUrl = this.fullPicUrl,
        smallUrl = this.smallUrl,
        topic = this.topic
    )
}
