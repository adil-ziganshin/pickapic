package com.example.pickapic.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
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
import androidx.navigation.NavController
import com.example.pickapic.R
import com.example.pickapic.ui.navigation.FavouritePicRoute
import com.example.pickapic.ui.navigation.PicturesRoute
import com.example.pickapic.ui.composables.TitleCard
import com.example.pickapic.ui.items.TopicItem
import com.example.pickapic.ui.theme.*
import com.example.pickapic.util.TopicModel

@Composable
fun HomeScreen(navController: NavController) {
    PickapicTheme {
        Scaffold(
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = contentColorFor(SnackbarDefaults.backgroundColor)
                .takeOrElse { LocalContentColor.current },
            floatingActionButton = { FloatingActButton(navController) },
            floatingActionButtonPosition = FabPosition.End
        ) {


            Column(
                modifier = Modifier
            ) {
                TitleCard(stringResource(id = R.string.home_title), Pencil700)
                TopicsRow(navController)
                SearchBar(navController)
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
private fun FloatingActButton(navController: NavController) {
    ExtendedFloatingActionButton(
        onClick = {
            navController.navigate(FavouritePicRoute)
        },
        text = { Text(text = "Favourites") },
        icon = {
            Icon(
                imageVector = Icons.Rounded.Favorite,
                contentDescription = "Favourite pictures"
            )
        }
    )
}

@Composable
private fun TopicsRow(navController: NavController) {

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
                TopicItem(item = item, navController = navController)
            }
        }

    }
}

@Composable
private fun SearchBar(
    navController: NavController
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
        TextField(modifier = Modifier
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
                        navController.navigate(PicturesRoute(topic = inputValue.value.text))
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
                    navController.navigate(PicturesRoute(topic = inputValue.value.text))
                }
            ),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                cursorColor = Color.White.copy(alpha = ContentAlpha.medium)
            ))
    }
}