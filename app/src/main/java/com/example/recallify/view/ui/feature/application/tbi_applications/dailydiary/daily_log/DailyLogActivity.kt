package com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_log

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.compose.ui.platform.ComposeView
import com.example.recallify.R
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_log.navigation.AudioVlogNavigation
import com.example.recallify.view.ui.theme.RecallifyTheme

class DailyLogActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_log)

        val dailyLogCompose : ComposeView = findViewById(R.id.activity_daily_diary_daily_log_screen)
        dailyLogCompose.setContent {
            RecallifyTheme {
                AudioVlogNavigation()
            }
        }
    }
}