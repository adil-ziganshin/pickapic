package com.example.pickapic.feature.favorites.data

import com.gsgroup.feature_favorites_api.entity.FavoritePicture
import com.gsgroup.feature_favorites_api.usecase.RemoveFromFavoritesUseCase
import javax.inject.Inject

class RemoveFromFavoritesUseCaseImpl @Inject constructor(
    private val favoritePicturesRepository: FavoritePicturesRepository
) : RemoveFromFavoritesUseCase {
    override suspend fun invoke(picture: FavoritePicture) {
        favoritePicturesRepository.removeFromFavorites(picture = picture)
    }
}
