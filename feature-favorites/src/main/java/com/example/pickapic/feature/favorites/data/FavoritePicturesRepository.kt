package com.example.pickapic.feature.favorites.data

import com.gsgroup.feature_favorites_api.entity.FavoritePicture
import kotlinx.coroutines.flow.Flow

interface FavoritePicturesRepository {

    fun fetchPictures(): Flow<List<FavoritePicture>>

    suspend fun addToFavorites(picture: FavoritePicture)

    suspend fun removeFromFavorites(picture: FavoritePicture)
}