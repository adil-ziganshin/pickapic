package com.example.pickapic.feature.favorites.data

import com.example.pickapic.feature.favorites.domain.FavoritePicture
import com.example.pickapic.feature.favorites.domain.FavoritePicturesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoritePicturesRepositoryImpl @Inject constructor(
    private val database: FavoritePictureDatabase
): FavoritePicturesRepository {

    override fun fetchPictures(): Flow<List<FavoritePicture>> =
        database
            .imageDao()
            .getAllPictures()
            .map { list ->
                list.map { it.toFavoritePicture() }
            }
}