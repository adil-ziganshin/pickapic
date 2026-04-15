package com.example.pickapic.feature.pictures

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pickapic.core.data.PictureRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class PicturesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: PictureRepositoryImpl
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

    fun onPictureClick() {

    }

    fun onErrorDismiss() {
        _uiState.value = PicturesScreenState.Empty(topic = topic)
    }

    fun onPicturePreview(pictureUrl: String) {
        val state = uiState.value
        if (state is PicturesScreenState.Loaded) {
            _uiState.value = state.copy(previewUrl = pictureUrl)
        } else {
            Log.d(tag, "onPicturePreview: wrong screen state")
        }
    }

    fun onDismissPreview() {
        val state = uiState.value
        if (state is PicturesScreenState.Loaded) {
            _uiState.value = state.copy(previewUrl = null)
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
