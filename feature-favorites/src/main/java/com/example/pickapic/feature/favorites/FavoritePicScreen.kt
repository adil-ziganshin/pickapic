package com.example.pickapic.feature.favorites

import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.res.stringResource
import com.example.pickapic.uikit.components.TitleCard
import com.example.pickapic.uikit.theme.Pencil700
import com.example.pickapic.uikit.theme.PickapicTheme

@Composable
fun FavoritePicScreen() {
    PickapicTheme {
        Scaffold(
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = contentColorFor(SnackbarDefaults.backgroundColor)
                .takeOrElse { LocalContentColor.current }
        ) {
            Column(
                modifier = Modifier
            ) {
                TitleCard(text = stringResource(id = R.string.fav_title), Pencil700)
            }
        }
    }
}
