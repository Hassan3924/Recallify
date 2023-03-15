package com.example.recallify.view.common.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.recallify.R

enum class TabPage(val icon: Int) {
    Activity(R.drawable.daily_activity),
    Log(R.drawable.daily_log)
}

@Composable
fun TabDiary(selectTabIndex: Int, onSelectTab: (TabPage) -> Unit) {
    TabRow(
        selectedTabIndex = selectTabIndex,
        contentColor = MaterialTheme.colors.primary,
        backgroundColor = MaterialTheme.colors.background,
        indicator = { tabPositions ->
            TabRowDefaults.Indicator(
                color = MaterialTheme.colors.onSurface,
                height = 2.dp,
                modifier = Modifier.tabIndicatorOffset(tabPositions[selectTabIndex])
            )
        }
    ) {
        TabPage.values().forEachIndexed { index, tabPage ->
            Tab(
                selected = index == selectTabIndex,
                onClick = { onSelectTab(tabPage) },
                icon = {
                    Icon(
                        painterResource(id = tabPage.icon),
                        contentDescription = "tab icon(activity or log)",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colors.onSurface
                    )
                },
                selectedContentColor = MaterialTheme.colors.secondary,
            )
        }
    }
}