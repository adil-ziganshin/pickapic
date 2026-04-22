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
import kotlinx.coroutines.Job
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

    private var currentPage: Int = 0
    private var totalPages: Int = Int.MAX_VALUE
    private var loadJob: Job? = null

    init {
        fetchFirstPage()
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
                    thumbUrl = previewState.thumbUrl,
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

    fun loadNextPage() {
        val state = uiState.value as? PicturesScreenState.Loaded ?: return
        if (state.data.isLoadingMore || state.data.endReached) return
        if (loadJob?.isActive == true) return
        if (currentPage >= totalPages) return

        _uiState.value = state.copy(data = state.data.copy(isLoadingMore = true))
        loadJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val nextPage = currentPage + 1
                val response = repository.getData(
                    query = topic,
                    page = nextPage,
                    perPage = PER_PAGE
                )
                currentPage = nextPage
                totalPages = response.totalPages

                val currentState = uiState.value
                if (currentState is PicturesScreenState.Loaded) {
                    val existingKeys = currentState.data.pictures.mapTo(HashSet()) { it.regularUrl }
                    val newItems = response.results
                        .map { it.toUiItem() }
                        .filter { existingKeys.add(it.regularUrl) }
                    _uiState.value = currentState.copy(
                        data = currentState.data.copy(
                            pictures = currentState.data.pictures + newItems,
                            isLoadingMore = false,
                            endReached = currentPage >= totalPages || response.results.isEmpty()
                        )
                    )
                }
            } catch (e: Exception) {
                handleLoadMoreError(e)
            }
        }
    }

    private fun fetchFirstPage() {
        _uiState.value = PicturesScreenState.Loading(title = title)
        currentPage = 0
        totalPages = Int.MAX_VALUE
        loadJob?.cancel()
        loadJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val firstPage = 1
                val response = repository.getData(
                    query = topic,
                    page = firstPage,
                    perPage = PER_PAGE
                )
                currentPage = firstPage
                totalPages = response.totalPages

                _uiState.value = PicturesScreenState.Loaded(
                    title = title,
                    data = PicturesUiModel(
                        pictures = response.results.map { it.toUiItem() },
                        isLoadingMore = false,
                        endReached = currentPage >= totalPages || response.results.isEmpty()
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

    private fun handleLoadMoreError(e: Exception) {
        val state = uiState.value
        if (state is PicturesScreenState.Loaded) {
            // Leave the existing feed, just stop the spinner so the user can retry on next scroll.
            _uiState.value = state.copy(data = state.data.copy(isLoadingMore = false))
        }
        if (e is HttpException && e.code() == 429) {
            onQueryLimitReached()
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

    private fun com.example.pickapic.core.data.Result.toUiItem(): PictureUiItem =
        PictureUiItem(
            thumbUrl = urls.thumb,
            smallUrl = urls.small,
            regularUrl = urls.regular,
            fullUrl = urls.full
        )

    companion object {
        private const val PER_PAGE = 30
    }
}
