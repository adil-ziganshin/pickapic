package com.example.pickapic.feature.favorites.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.gsgroup.feature_favorites_api.entity.FavoritePicture

@Entity(tableName = "pictures")
data class PictureRoomEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val previewUrl: String,
    val fullPicUrl: String,
    val smallUrl: String,
    val thumbUrl: String,
    val topic: String
)

fun PictureRoomEntity.toFavoritePicture() = FavoritePicture(
    previewUrl = this.previewUrl,
    fullPicUrl = this.fullPicUrl,
    smallUrl = this.smallUrl,
    topic = this.topic,
    thumbUrl = this.thumbUrl
)

fun FavoritePicture.toRoomEntity() = PictureRoomEntity(
    previewUrl = this.previewUrl,
    fullPicUrl = this.fullPicUrl,
    smallUrl = this.smallUrl,
    topic = this.topic,
    thumbUrl = this.thumbUrl
)
