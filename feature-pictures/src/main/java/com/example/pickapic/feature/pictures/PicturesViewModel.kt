package com.example.pickapic.feature.pictures

import android.annotation.SuppressLint
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

    val topic: String = savedStateHandle.get<String>("topic") ?: "Unknown"
    private val _uiState = MutableStateFlow<PicturesScreenState>(
        PicturesScreenState.Empty(topic = topic)
    )
    val uiState: StateFlow<PicturesScreenState> = _uiState.asStateFlow()

    init {
        fetchPictures()
    }

    private fun fetchPictures() {
        _uiState.value = PicturesScreenState.Loading(topic = topic)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getData(topic)
                _uiState.value = PicturesScreenState.Loaded(
                    topic = topic,
                    PicturesUiModel(
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

    sealed interface PicturesScreenState {

        val topic: String

        data class Empty(override val topic: String) : PicturesScreenState

        data class Loading(override val topic: String) : PicturesScreenState

        data class Loaded(
            override val topic: String,
            val data: PicturesUiModel
        ) : PicturesScreenState

        data class Error(
            override val topic: String,
            val message: String
        ) : PicturesScreenState
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
