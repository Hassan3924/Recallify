package com.example.recallify.view.common.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DiaryActivityTopAppBar(
    context: Context,
    navToDailyFeed: @Composable()  (() -> Unit)
) {
    Box(
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
    ) {
        TopAppBar(
            title = { Text("Diary activity") },
            backgroundColor = MaterialTheme.colors.background,
            modifier = Modifier.fillMaxWidth(),
            navigationIcon = { navToDailyFeed() }
        )
    }
}