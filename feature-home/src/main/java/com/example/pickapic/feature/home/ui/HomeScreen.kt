package com.example.pickapic.feature.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.FabPosition
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.pickapic.feature.home.R
import com.example.pickapic.uikit.components.TitleCard
import com.example.pickapic.uikit.theme.Pencil700
import com.example.pickapic.uikit.theme.PickapicTheme
import com.example.pickapic.uikit.theme.Shapes
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun HomeScreenRoute(
    onPerformSearch: (String) -> Unit,
    onFavoriteButtonClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    HomeScreen(
        state = state,
        onPerformSearch = onPerformSearch,
        onFavoriteButtonClick = onFavoriteButtonClick,
        onLoadMore = viewModel::loadNextPage,
        onErrorDismiss = viewModel::onErrorDismiss,
    )
}

@Composable
private fun HomeScreen(
    state: HomeScreenState,
    onPerformSearch: (String) -> Unit,
    onFavoriteButtonClick: () -> Unit,
    onLoadMore: () -> Unit,
    onErrorDismiss: () -> Unit,
) {
    PickapicTheme {
        Scaffold(
            backgroundColor = MaterialTheme.colors.primary,
            modifier = Modifier,
            floatingActionButton = {
                FloatingFavoritesButton(onClick = onFavoriteButtonClick)
            },
            floatingActionButtonPosition = FabPosition.End
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                TitleCard(
                    text = stringResource(id = R.string.home_title),
                    color = Pencil700
                )
                TopicsRow(
                    topics = state.topics,
                    isInitialLoading = state.isInitialLoading,
                    isLoadingMore = state.isLoadingMore,
                    endReached = state.endReached,
                    onTopicChosen = onPerformSearch,
                    onLoadMore = onLoadMore,
                )
                SearchBar(onPerformSearch = onPerformSearch)
            }

            if (state.errorMessage != null) {
                ErrorDialog(message = state.errorMessage, onDismiss = onErrorDismiss)
            }
        }
    }

}

@Composable
private fun FloatingFavoritesButton(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        text = { Text(text = "Favorites") },
        icon = {
            Icon(
                imageVector = Icons.Rounded.Favorite,
                contentDescription = "Favorite pictures"
            )
        }
    )
}

@Composable
private fun TopicsRow(
    topics: List<TopicModel>,
    isInitialLoading: Boolean,
    isLoadingMore: Boolean,
    endReached: Boolean,
    onTopicChosen: (topic: String) -> Unit,
    onLoadMore: () -> Unit,
) {
    val listState = rememberLazyListState()
    InfiniteHorizontalListHandler(
        listState = listState,
        totalItems = topics.size,
        isLoadingMore = isLoadingMore,
        endReached = endReached,
        onLoadMore = onLoadMore,
    )

    Column(modifier = Modifier.padding(2.dp)) {
        Text(
            text = "Topics",
            style = MaterialTheme.typography.h4,
            textAlign = TextAlign.End,
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 32.dp,
            )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
        ) {
            when {
                isInitialLoading && topics.isEmpty() -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )

                topics.isEmpty() -> Text(
                    text = stringResource(id = com.example.pickapic.uikit.R.string.no_data_available),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(16.dp)
                )

                else -> LazyRow(
                    state = listState,
                    flingBehavior = rememberHeavyFlingBehavior(),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(
                        items = topics,
                        key = { it.id },
                    ) { item ->
                        TopicItem(
                            item = item,
                            onClick = onTopicChosen,
                        )
                    }
                    if (isLoadingMore) {
                        item(key = "topics_loading_more") {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 24.dp),
                                contentAlignment = Alignment.Center,
                            ) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun rememberHeavyFlingBehavior(
    velocityMultiplier: Float = 0.45f,
): FlingBehavior {
    val defaultFlingBehavior = ScrollableDefaults.flingBehavior()
    return remember(defaultFlingBehavior, velocityMultiplier) {
        object : FlingBehavior {
            override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
                return with(defaultFlingBehavior) {
                    performFling(initialVelocity * velocityMultiplier)
                }
            }
        }
    }
}

@Composable
private fun InfiniteHorizontalListHandler(
    listState: LazyListState,
    totalItems: Int,
    isLoadingMore: Boolean,
    endReached: Boolean,
    onLoadMore: () -> Unit,
    prefetchDistance: Int = 3,
) {
    val currentOnLoadMore by rememberUpdatedState(onLoadMore)
    val shouldLoadMore = remember(listState, totalItems) {
        snapshotFlow {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1
            totalItems > 0 && lastVisible >= totalItems - 1 - prefetchDistance
        }.distinctUntilChanged().filter { it }
    }

    LaunchedEffect(shouldLoadMore, isLoadingMore, endReached) {
        if (isLoadingMore || endReached) return@LaunchedEffect
        shouldLoadMore.collect { reached ->
            if (reached) currentOnLoadMore()
        }
    }
}

@Composable
private fun SearchBar(onPerformSearch: (String) -> Unit) {
    val inputValue = remember { mutableStateOf(TextFieldValue()) }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(start = 20.dp, end = 20.dp),
        elevation = AppBarDefaults.TopAppBarElevation,
        color = Pencil700,
        shape = Shapes.large
    ) {
        TextField(
            modifier = Modifier.fillMaxSize(),
            value = inputValue.value,
            onValueChange = { inputValue.value = it },
            placeholder = {
                Text(
                    modifier = Modifier.alpha(ContentAlpha.medium),
                    text = "Search here...",
                    color = Color.White
                )
            },
            textStyle = MaterialTheme.typography.body1,
            singleLine = true,
            trailingIcon = {
                IconButton(
                    modifier = Modifier.alpha(ContentAlpha.medium),
                    onClick = { onPerformSearch(inputValue.value.text) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = Color.White
                    )
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = { onPerformSearch(inputValue.value.text) }
            ),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                cursorColor = Color.White.copy(alpha = ContentAlpha.medium)
            )
        )
    }
}

@Composable
private fun ErrorDialog(message: String, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(com.example.pickapic.uikit.R.string.problem_occurred)) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(android.R.string.ok))
            }
        }
    )
}
