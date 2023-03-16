 package com.example.recallify.view.common.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.recallify.R


 @Composable
 fun DiaryTopAppBarGuardian(
     clickFilter: () -> Unit,
 ) {
     Box(
         modifier = Modifier
             .height(56.dp)
             .fillMaxWidth()
     ) {
         TopAppBar(
             title = { Text("Daily Diary") },
             backgroundColor = MaterialTheme.colors.background,
             modifier = Modifier.fillMaxWidth(),
             actions = {
                 FilterAction(
                     onClickFilter = clickFilter
                 )
             }
         )
     }
 }
@Composable
fun DiaryTopAppBar(
    clickCreate: () -> Unit,
    clickFilter: () -> Unit,
) {
    Box(
        modifier = Modifier
            .height(56.dp)
            .fillMaxWidth()
    ) {
        TopAppBar(
            title = { Text("Daily Diary") },
            backgroundColor = MaterialTheme.colors.background,
            modifier = Modifier.fillMaxWidth(),
            actions = {
                FilterAction(
                    onClickFilter = clickFilter
                )
                CreateAction(
                    onClickCreate = clickCreate
                )
            }
        )
    }
}


/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
@Composable
fun FilterAction(onClickFilter: () -> Unit) {
    IconButton(
        modifier = Modifier.padding(horizontal = 12.dp),
        onClick = { onClickFilter() }
    ) {
        Icon(
            painter = painterResource(id = R.drawable.filter),
            contentDescription = "filter",
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colors.onSurface
        )
    }
}

@Composable

fun CreateAction(onClickCreate: () -> Unit) {
    IconButton(
        modifier = Modifier.padding(horizontal = 12.dp),
        onClick = { onClickCreate() }
    ) {
        Icon(
            painter = painterResource(id = R.drawable.create_48),
            contentDescription = "create",
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colors.onSurface
        )
    }
}