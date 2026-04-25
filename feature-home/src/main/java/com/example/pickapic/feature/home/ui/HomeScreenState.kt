package com.example.pickapic.feature.home.ui

data class HomeScreenState(
    val topics: List<TopicModel> = emptyList(),
    val isInitialLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val endReached: Boolean = false,
    val errorMessage: String? = null,
)
