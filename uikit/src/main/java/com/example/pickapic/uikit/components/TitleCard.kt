package com.example.pickapic.uikit.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.pickapic.uikit.theme.SemiRoundedShapes

@Composable
fun TitleCard(
    text: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        elevation = 16.dp,
        shape = SemiRoundedShapes.large,
        backgroundColor = color
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.h4,
            textAlign = TextAlign.End,
            modifier = Modifier
                .statusBarsPadding()
                .padding(24.dp)
        )
    }
}