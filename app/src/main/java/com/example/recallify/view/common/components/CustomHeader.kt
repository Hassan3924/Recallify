package com.example.recallify.view.common.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun RecallifyCustomHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.caption.copy(
            fontWeight = MaterialTheme.typography.body1.fontWeight,
            fontSize = MaterialTheme.typography.body1.fontSize
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        color = MaterialTheme.colors.onSurface.copy(
            alpha = ContentAlpha.medium,
        )
    )
}

@Composable
fun RecallifyCustomHeader2(
    title: String
) {
    Text(
        text = title,
        style = MaterialTheme.typography.body1.copy(
            fontWeight = FontWeight.Medium
        )
    )
}