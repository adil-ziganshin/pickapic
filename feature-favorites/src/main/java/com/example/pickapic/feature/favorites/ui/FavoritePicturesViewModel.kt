package com.example.pickapic.feature.favorites.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pickapic.feature.favorites.domain.FavoritePicture
import com.example.pickapic.feature.favorites.domain.FavoritePicturesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class FavoritePicturesViewModel(
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