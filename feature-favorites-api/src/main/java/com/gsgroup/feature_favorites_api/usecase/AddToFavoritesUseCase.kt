package com.gsgroup.feature_favorites_api.usecase

import com.gsgroup.feature_favorites_api.entity.FavoritePicture

interface AddToFavoritesUseCase {
    suspend operator fun invoke(picture: FavoritePicture)
}