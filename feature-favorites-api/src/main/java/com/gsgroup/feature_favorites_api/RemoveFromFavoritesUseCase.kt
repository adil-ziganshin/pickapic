package com.gsgroup.feature_favorites_api

interface RemoveFromFavoritesUseCase {
    suspend operator fun invoke(picture: FavoritePicture)
}
