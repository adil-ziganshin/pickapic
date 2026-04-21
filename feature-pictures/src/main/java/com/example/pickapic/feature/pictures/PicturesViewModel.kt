package com.example.pickapic.feature.pictures

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pickapic.core.data.PictureRepositoryImpl
import com.example.pickapic.uikit.pictures.PictureUiItem
import com.example.pickapic.uikit.pictures.PicturesScreenState
import com.example.pickapic.uikit.pictures.PicturesUiModel
import com.example.pickapic.uikit.pictures.PreviewState
import com.example.pickapic.wallpaper.WallpaperInteractor
import com.gsgroup.feature_favorites_api.AddToFavoritesUseCase
import com.gsgroup.feature_favorites_api.FavoritePicture
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class PicturesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: PictureRepositoryImpl,
    private val wallpaperInteractor: WallpaperInteractor,
    private val addToFavoritesUseCase: AddToFavoritesUseCase
) : ViewModel() {

    private val tag = "PicturesViewModel"
    val topic: String = savedStateHandle.get<String>("topic") ?: "Unknown"
    private val title: String = if (topic.length < 14) topic else "Search"

    private val _uiState = MutableStateFlow<PicturesScreenState>(
        PicturesScreenState.Empty(title = title)
    )
    val uiState: StateFlow<PicturesScreenState> = _uiState.asStateFlow()

    init {
        fetchPictures()
    }

    fun onErrorDismiss() {
        _uiState.value = PicturesScreenState.Empty(title = title)
    }

    fun onSetWallpaper(pictureUrl: String) {
        viewModelScope.launch {
            val state = uiState.value
            if (state is PicturesScreenState.Loaded) {
                _uiState.value = state.copy(preview = state.preview?.copy(settingWallpaper = true))
            }
            val setWallpaperResult = wallpaperInteractor.setWallpaper(pictureUrl = pictureUrl)
            setWallpaperResult.fold(
                onSuccess = {
                    val currentState = uiState.value
                    if (currentState is PicturesScreenState.Loaded) {
                        _uiState.value = currentState.copy(
                            preview = currentState.preview?.copy(
                                isWallpaperSet = true,
                                settingWallpaper = false
                            )
                        )
                    }
                },
                onFailure = {
                    _uiState.value = PicturesScreenState.Error(title = title, message = it.message)
                }
            )
        }
    }

    fun onPicturePreview(previewState: PreviewState) {
        val state = uiState.value
        if (state is PicturesScreenState.Loaded) {
            _uiState.value = state.copy(preview = previewState)
        } else {
            Log.d(tag, "onPicturePreview: wrong screen state")
        }
    }

    fun onPreviewPictureDoubleTap(previewState: PreviewState) {
        viewModelScope.launch {
            addToFavoritesUseCase.invoke(
                picture = FavoritePicture(
                    previewUrl = previewState.previewUrl,
                    fullPicUrl = previewState.fullPictureUrl,
                    smallUrl = previewState.smallUrl,
                    topic = topic
                )
            )
        }
    }

    fun onDismissPreview() {
        val state = uiState.value
        if (state is PicturesScreenState.Loaded) {
            _uiState.value = state.copy(preview = null)
        } else {
            Log.d(tag, "onPicturePreview: wrong screen state")
        }
    }


    private fun fetchPictures() {
        _uiState.value = PicturesScreenState.Loading(title = title)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getData(topic)
                _uiState.value = PicturesScreenState.Loaded(
                    title = title,
                    data = PicturesUiModel(
                        pictures = response.results.map { result ->
                            PictureUiItem(
                                smallUrl = result.urls.small,
                                regularUrl = result.urls.regular,
                                fullUrl = result.urls.full
                            )
                        }
                    )
                )
            } catch (e: Exception) {
                if (e is HttpException && e.code() == 429) {
                    onQueryLimitReached()
                } else {
                    onErrorOccurred()
                }
            }
        }
    }

    private fun onQueryLimitReached() {
        _uiState.value = PicturesScreenState.Error(
            title = title,
            message = "Query Limit Reached"
        )
    }

    private fun onErrorOccurred() {
        _uiState.value = PicturesScreenState.Error(
            title = title,
            message = "Something went wrong"
        )
    }
}
