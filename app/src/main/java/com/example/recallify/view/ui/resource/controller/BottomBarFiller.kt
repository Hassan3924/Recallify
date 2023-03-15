package com.example.recallify.view.ui.resource.controller

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BottomBarFiller() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .width(35.dp)
            .background(Color.Transparent)
    ) {
        BottomAppBar(
            backgroundColor = Color.Transparent,
            contentColor = Color.Transparent,
            cutoutShape = null,
            elevation = 0.dp,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "_")
            }
        }
    }
}