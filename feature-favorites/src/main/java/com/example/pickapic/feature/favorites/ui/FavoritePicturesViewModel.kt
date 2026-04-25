package com.example.pickapic.feature.favorites.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pickapic.feature.favorites.R
import com.example.pickapic.feature.favorites.data.FavoritePicturesRepository
import com.example.pickapic.uikit.pictures.PictureUiItem
import com.example.pickapic.uikit.pictures.PicturesGridState
import com.example.pickapic.uikit.pictures.PicturesUiModel
import com.example.pickapic.uikit.pictures.PreviewState
import com.example.pickapic.wallpaper.domain.SetWallpaperUseCase
import com.gsgroup.feature_favorites_api.entity.FavoritePicture
import com.gsgroup.feature_favorites_api.usecase.RemoveFromFavoritesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritePicturesViewModel @Inject constructor(
    @ApplicationContext context: Context,
    favoritePicturesRepository: FavoritePicturesRepository,
    private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase,
    private val setWallpaperUseCase: SetWallpaperUseCase
) : ViewModel() {

    private val tag = "FavoritePicturesVM"
    private val title: String = context.getString(R.string.fav_title)

    private val preview = MutableStateFlow<PreviewState?>(null)
    private val error = MutableStateFlow<String?>(null)

    val uiState: StateFlow<PicturesGridState> =
        combine(
            favoritePicturesRepository.fetchPictures(),
            preview,
            error
        ) { favorites, previewState, errorMessage ->
            when {
                errorMessage != null -> PicturesGridState.Error(
                    title = title,
                    message = errorMessage
                )
                favorites.isEmpty() -> PicturesGridState.Empty(title = title)

                else -> PicturesGridState.Loaded(
                    title = title,
                    data = PicturesUiModel(
                        pictures = favorites.map { it.toPictureUiItem() }
                    ),
                    preview = previewState
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = PicturesGridState.Loading(title = title)
        )

    fun onPicturePreview(previewState: PreviewState) {
        val state = uiState.value
        if (state is PicturesGridState.Loaded) {
            preview.value = previewState
        } else {
            Log.d(tag, "onPicturePreview: wrong screen state")
        }
    }

    fun onDismissPreview() {
        preview.value = null
    }

    fun onPictureLongClick(item: PictureUiItem) {
        viewModelScope.launch {
            removeFromFavoritesUseCase.invoke(picture = item.toFavoritePicture())
        }
    }

    fun onPreviewPictureDoubleTap(previewState: PreviewState) {
        viewModelScope.launch {
            removeFromFavoritesUseCase.invoke(picture = previewState.toFavoritePicture())
            preview.value = null
        }
    }

    fun onSetWallpaper(pictureUrl: String) {
        viewModelScope.launch {
            preview.value = preview.value?.copy(settingWallpaper = true)
            val setWallpaperResult = setWallpaperUseCase.setWallpaper(pictureUrl = pictureUrl)
            setWallpaperResult.fold(
                onSuccess = {
                    preview.value = preview.value?.copy(
                        isWallpaperSet = true,
                        settingWallpaper = false
                    )
                },
                onFailure = { throwable ->
                    preview.value = null
                    error.value = throwable.message
                }
            )
        }
    }

    fun onErrorDismiss() {
        error.value = null
    }

    private fun FavoritePicture.toPictureUiItem() = PictureUiItem(
        smallUrl = smallUrl,
        regularUrl = previewUrl,
        fullUrl = fullPicUrl,
        thumbUrl = thumbUrl
    )

    private fun PictureUiItem.toFavoritePicture() = FavoritePicture(
        previewUrl = regularUrl,
        fullPicUrl = fullUrl,
        smallUrl = smallUrl,
        thumbUrl = thumbUrl,
        topic = ""
    )

    private fun PreviewState.toFavoritePicture() = FavoritePicture(
        previewUrl = previewUrl,
        fullPicUrl = fullPictureUrl,
        smallUrl = smallUrl,
        thumbUrl = thumbUrl,
        topic = ""
    )
}
