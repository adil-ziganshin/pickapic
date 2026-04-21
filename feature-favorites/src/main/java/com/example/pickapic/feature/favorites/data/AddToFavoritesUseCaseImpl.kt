package com.example.pickapic.feature.favorites.data

import com.gsgroup.feature_favorites_api.AddToFavoritesUseCase
import com.gsgroup.feature_favorites_api.FavoritePicture
import javax.inject.Inject

class AddToFavoritesUseCaseImpl @Inject constructor(
    private val favoritePicturesRepository: FavoritePicturesRepository
): AddToFavoritesUseCase {
    override suspend operator fun invoke(picture: FavoritePicture) {
        favoritePicturesRepository.addToFavorites(picture = picture)
    }
}