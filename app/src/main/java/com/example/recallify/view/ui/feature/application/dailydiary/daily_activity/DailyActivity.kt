package com.example.recallify.view.ui.feature.application.dailydiary.daily_activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.recallify.R
import com.example.recallify.view.common.components.DiaryActivityTopAppBar
import com.example.recallify.view.ui.theme.RecallifyTheme

class DailyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily)
        val dailyActivityCompose: ComposeView = findViewById(R.id.activity_daily_diary_daily_activity_screen)
        dailyActivityCompose.setContent {
            RecallifyTheme {
                DailyActivityScreen(

                )
            }
        }
    }

    @Composable
    fun DailyActivityScreen() {
        Scaffold(
            scaffoldState = rememberScaffoldState(),
            topBar = { DiaryActivityTopAppBar() },
            backgroundColor = MaterialTheme.colors.surface
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues),
                verticalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 4.dp)
                        .padding(bottom = 8.dp)
                ) {
                    // fixme: add activity body here

                }
            }
        }
    }

    @Preview
    @Composable
    fun DailyActivityPreview() {
        RecallifyTheme {
            DailyActivityScreen()
        }
    }
}