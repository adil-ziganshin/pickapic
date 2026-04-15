package com.example.pickapic.feature.pictures

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.pickapic.uikit.theme.Pencil700
import com.example.pickapic.uikit.theme.PickapicTheme

@Composable
fun FullPicScreen(
    pictureUrl: String
) {
    PickapicTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.primary)
        ) {
            AsyncImage(
                model = pictureUrl,
                contentDescription = "Image",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Pencil700,
                    contentColor = MaterialTheme.colors.onPrimary
                )
            ) {
                Text(text = stringResource(id = R.string.set_this_wallpaper))
            }
        }
    }
}
