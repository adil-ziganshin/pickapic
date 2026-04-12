package com.example.pickapic.ui.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pickapic.ui.theme.SemiRoundedShapes
import androidx.compose.ui.graphics.Color

@Composable
fun TitleCard(text: String, color: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = 16.dp,
        shape = SemiRoundedShapes.large,
        backgroundColor = color
    ) {
        TitleText(text)
    }
}

@Composable
private fun TitleText(text: String) {

    Text(
        text = text,
        style = MaterialTheme.typography.h4,
        textAlign = TextAlign.End,
        modifier = Modifier.padding(
            horizontal = 24.dp,
            vertical = 24.dp
        )
    )
}