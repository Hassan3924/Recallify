package com.example.recallify.view.common.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recallify.R

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomFaq(
    title: String,
    message: String,
) {
    var isExpanded by remember {
        mutableStateOf(false)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            ),
        shape = RoundedCornerShape(6.dp),
        backgroundColor = MaterialTheme.colors.background,
        onClick = {
            isExpanded = !isExpanded // true!
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp)
                .padding(vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.body2.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    maxLines = 3,
                    softWrap = true,
                    modifier = Modifier.width(250.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(
                        onClick = {
                            isExpanded = !isExpanded
                        }
                    ) {
                        if (!isExpanded) {
                            Icon(
                                painter = painterResource(id = R.drawable.round_arrow_left_24),
                                contentDescription = null
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.round_arrow_drop_down_24),
                                contentDescription = null
                            )
                        }
                    }
                }
            }
            if (isExpanded) {
                Text(
                    text = message,
                    style = MaterialTheme.typography.body2.copy(
                        fontSize = 12.sp
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp,
                        end = 10.dp),
                    textAlign = TextAlign.Justify,
                )
            }
        }
    }
}