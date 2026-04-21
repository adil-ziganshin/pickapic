package com.example.pickapic.feature.pictures

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pickapic.core.data.PictureRepositoryImpl
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
    private val _uiState = MutableStateFlow<PicturesScreenState>(
        PicturesScreenState.Empty(topic = topic)
    )
    val uiState: StateFlow<PicturesScreenState> = _uiState.asStateFlow()

    init {
        fetchPictures()
    }

    fun onErrorDismiss() {
        _uiState.value = PicturesScreenState.Empty(topic = topic)
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
                    val state = uiState.value
                    if (state is PicturesScreenState.Loaded) {
                        _uiState.value = state.copy(
                            preview = state.preview?.copy(
                                isWallpaperSet = true,
                                settingWallpaper = false
                            )
                        )
                    }
                },
                onFailure = {
                    _uiState.value = PicturesScreenState.Error(topic = topic, message = it.message)
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
        _uiState.value = PicturesScreenState.Loading(topic = topic)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getData(topic)
                _uiState.value = PicturesScreenState.Loaded(
                    topic = topic,
                    data = PicturesUiModel(
                        pictures = response.results
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
            topic = topic,
            message = "Query Limit Reached"
        )
    }

    private fun onErrorOccurred() {
        _uiState.value = PicturesScreenState.Error(
            topic = topic,
            message = "Something went wrong"
        )
    }
}
