package com.example.pickapic.feature.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AppBarDefaults
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
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pickapic.core.util.TopicModel
import com.example.pickapic.uikit.components.TitleCard
import com.example.pickapic.uikit.theme.Pencil700
import com.example.pickapic.uikit.theme.PickapicTheme
import com.example.pickapic.uikit.theme.Shapes

@Composable
fun HomeScreen(
    onPerformSearch: (String) -> Unit,
    onFavoriteButtonClick: () -> Unit
) {
    PickapicTheme {
        Scaffold(
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = contentColorFor(SnackbarDefaults.backgroundColor)
                .takeOrElse { LocalContentColor.current },
            floatingActionButton = {
                FloatingFavoritesButton(onClick = onFavoriteButtonClick)
            },
            floatingActionButtonPosition = FabPosition.End
        ) {
            Column(
                modifier = Modifier
            ) {
                TitleCard(stringResource(id = R.string.home_title), Pencil700)
                TopicsRow(onTopicChosen = onPerformSearch)
                SearchBar(onPerformSearch = onPerformSearch)
            }
        }
    }
}

private val topicsList = listOf(
    TopicModel("Nature", R.drawable.img_nature),
    TopicModel("Flowers", R.drawable.img_flowers),
    TopicModel("Cozy", R.drawable.img_cozy),
    TopicModel("Animals", R.drawable.img_animals),
    TopicModel("Urban", R.drawable.img_urban)
)

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
    onTopicChosen: (topic: String) -> Unit
) {
    Column(
        modifier = Modifier.padding(2.dp)
    ) {
        Text(
            text = "Topics",
            style = MaterialTheme.typography.h4,
            textAlign = TextAlign.End,
            modifier = Modifier
                .padding(
                    horizontal = 16.dp,
                    vertical = 32.dp
                )
        )
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            itemsIndexed(
                topicsList
            ) { _, item ->
                TopicItem(
                    item = item,
                    onClick = onTopicChosen
                )
            }
        }
    }
}

@Composable
private fun SearchBar(
    onPerformSearch: (String) -> Unit
) {
    val inputValue = remember { mutableStateOf(TextFieldValue()) }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(
                start = 20.dp,
                end = 20.dp
            ),
        elevation = AppBarDefaults.TopAppBarElevation,
        color = Pencil700,
        shape = Shapes.large
    ) {
        TextField(
            modifier = Modifier
                .fillMaxSize(),
            value = inputValue.value,
            onValueChange = {
                inputValue.value = it
            },
            placeholder = {
                Text(
                    modifier = Modifier
                        .alpha(ContentAlpha.medium),
                    text = "Search here...",
                    color = Color.White
                )
            },
            textStyle = MaterialTheme.typography.body1,
            singleLine = true,
            trailingIcon = {
                IconButton(
                    modifier = Modifier
                        .alpha(ContentAlpha.medium),
                    onClick = {
                        onPerformSearch(inputValue.value.text)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = Color.White
                    )
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onPerformSearch(inputValue.value.text)
                }
            ),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                cursorColor = Color.White.copy(alpha = ContentAlpha.medium)
            )
        )
    }
}
