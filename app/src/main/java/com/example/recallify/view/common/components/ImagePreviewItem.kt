package com.example.recallify.view.common.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun ImagePreviewItem(
    uri: Uri,
    height: Dp,
    width: Dp,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomEnd
    ) {
        AsyncImage(
            model = uri,
            contentDescription = "",
            modifier = Modifier
                .width(width)
                .height(height)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            Arrangement.End,
            Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .clip(CircleShape)
                    .width(32.dp)
                    .height(32.dp)
                    .background(Color.White)
                    .padding(3.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Bottom
            ) {
                IconButton(
                    onClick = { onClick() }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "",
                        modifier = Modifier
                            .size(24.dp),
                        tint = Color.Red
                    )
                }
            }
        }
    }
}