package com.example.pickapic.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pickapic.core.domain.Topic
import com.example.pickapic.feature.home.domain.GetTopicsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTopicsUseCase: GetTopicsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeScreenState(isInitialLoading = true))
    val uiState: StateFlow<HomeScreenState> = _uiState.asStateFlow()

    private var currentPage: Int = 0
    private var loadJob: Job? = null

    init {
        loadNextPage()
    }

    fun onErrorDismiss() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun loadNextPage() {
        val state = _uiState.value
        if (state.isInitialLoading && state.topics.isNotEmpty()) return
        if (state.isLoadingMore || state.endReached) return
        if (loadJob?.isActive == true) return

        _uiState.update {
            if (it.topics.isEmpty()) it.copy(isInitialLoading = true)
            else it.copy(isLoadingMore = true)
        }

        loadJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val nextPage = currentPage + 1
                val topics = getTopicsUseCase(page = nextPage, perPage = PER_PAGE)
                currentPage = nextPage
                _uiState.update { current ->
                    val newItems = topics
                        .map(Topic::toUiModel)
                    current.copy(
                        topics = current.topics + newItems,
                        isInitialLoading = false,
                        isLoadingMore = false,
                        endReached = topics.size < PER_PAGE,
                    )
                }
            } catch (e: Exception) {
                val message = if (e is HttpException && e.code() == 429) {
                    "Query Limit Reached"
                } else {
                    "Something went wrong"
                }
                _uiState.update {
                    it.copy(
                        isInitialLoading = false,
                        isLoadingMore = false,
                        errorMessage = message,
                    )
                }
            }
        }
    }

    private companion object {
        const val PER_PAGE = 10
    }
}

private fun Topic.toUiModel(): TopicModel = TopicModel(
    id = id,
    title = title,
    coverUrl = coverSmallUrl
)
