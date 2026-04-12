package com.example.pickapic.ui.viewmodels

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pickapic.data.PictureRepositoryImpl
import com.example.pickapic.ui.items.PictureUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class PicturesViewModel @Inject constructor (
    private val repository: PictureRepositoryImpl
    ): ViewModel() {

    private val _uiState = MutableStateFlow<PicturesUiState>(PicturesUiState.Empty)
    val uiState: StateFlow<PicturesUiState> = _uiState

    private val _currentPictureUrl = MutableStateFlow("")
    val currentPictureUrl: StateFlow<String> = _currentPictureUrl


    fun fetchPicturesByTopic(topic: String) {
        _uiState.value = PicturesUiState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = repository.getData(topic)
                _uiState.value = PicturesUiState.Loaded(
                    PictureUiModel(
                        results = response.results
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

    sealed class PicturesUiState {
        object Empty : PicturesUiState()
        object Loading : PicturesUiState()
        class Loaded(val data: PictureUiModel) : PicturesUiState()
        class Error(val message: String) : PicturesUiState()
    }

    private fun onQueryLimitReached() {
        _uiState.value = PicturesUiState.Error(
            "Query Limit Reached"
        )
    }

    private fun onErrorOccurred() {
        _uiState.value = PicturesUiState.Error(
            "Something went wrong"
        )
    }

    fun updateCurrentPictureUrl(url: String) {
        _currentPictureUrl.value = url
    }
}
