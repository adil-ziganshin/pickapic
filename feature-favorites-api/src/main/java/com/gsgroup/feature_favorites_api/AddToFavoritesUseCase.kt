package com.gsgroup.feature_favorites_api

import com.gsgroup.feature_favorites_api.FavoritePicture

interface AddToFavoritesUseCase {
    suspend operator fun invoke(picture: FavoritePicture)
}