package com.example.recallify.view.common.resources

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun AccountsTopAppBar(
    onNavToHome: @Composable (() -> Unit)
) {
    Box(
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
    ) {
        TopAppBar(
            title = { Text("Accounts") },
            backgroundColor = MaterialTheme.colors.background,
            modifier = Modifier.fillMaxWidth(),
            actions = {
                onNavToHome()
            }
        )
    }
}

@Composable
fun GuardiansAccountTopAppBar(
    onNavToHome: @Composable (() -> Unit)
) {
    Box(
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
    ) {
        TopAppBar(
            title = { Text("Accounts") },
            backgroundColor = MaterialTheme.colors.background,
            modifier = Modifier.fillMaxWidth(),
            actions = {
                onNavToHome()
            }
        )
    }
}