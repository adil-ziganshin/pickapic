package com.example.pickapic.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.pickapic.uikit.theme.Pencil700
import com.example.pickapic.uikit.theme.Shapes

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TopicItem(
    item: TopicModel,
    onClick: (String) -> Unit,
) {
    Card(
        shape = Shapes.large,
        modifier = Modifier
            .padding(16.dp)
            .sizeIn(maxWidth = 240.dp, maxHeight = 240.dp),
        elevation = 12.dp,
        onClick = {
            onClick(item.title)
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            AsyncImage(
                modifier = Modifier.fillMaxSize(),
                model = item.coverUrl,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
            )
            Text(
                text = item.title,
                fontWeight = FontWeight.W900,
                fontSize = 32.sp,
                color = Pencil700,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 32.dp)
            )
        }
    }
}
