package com.example.pickapic.feature.favorites.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pickapic.feature.favorites.data.FavoritePicturesRepository
import com.gsgroup.feature_favorites_api.FavoritePicture
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FavoritePicturesViewModel @Inject constructor(
    favoritePicturesRepository: FavoritePicturesRepository
): ViewModel() {

    val favoritePicturesFlow: StateFlow<List<FavoritePicture>?> =
        favoritePicturesRepository
            .fetchPictures()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Eagerly,
                initialValue = null
            )
}