package com.example.pickapic.feature.favorites.domain

import com.example.pickapic.feature.favorites.data.PictureRoomEntity
import kotlinx.coroutines.flow.Flow

interface FavoritePicturesRepository {

    fun fetchPictures(): Flow<List<FavoritePicture>>
}