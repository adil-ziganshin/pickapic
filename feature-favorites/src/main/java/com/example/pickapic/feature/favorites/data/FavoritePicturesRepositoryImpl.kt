package com.example.pickapic.feature.favorites.data

import android.util.Log
import com.gsgroup.feature_favorites_api.FavoritePicture
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FavoritePicturesRepositoryImpl @Inject constructor(
    database: FavoritePictureDatabase
): FavoritePicturesRepository {

    val db = database.imageDao()

    override fun fetchPictures(): Flow<List<FavoritePicture>> =
        db.getAllPictures()
            .map { list ->
                list.map { it.toFavoritePicture() }
            }

    override suspend fun addToFavorites(picture: FavoritePicture) = withContext(Dispatchers.IO) {
        Log.d("FavoritePicturesRepositoryImpl", "addToFavorites")
        db.insertPicture(picture = picture.toRoomEntity())
    }
}