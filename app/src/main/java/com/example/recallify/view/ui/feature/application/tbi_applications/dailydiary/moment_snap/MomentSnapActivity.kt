package com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.moment_snap

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.platform.ComposeView
import com.example.recallify.R
import com.example.recallify.view.ui.theme.RecallifyTheme

class MomentSnapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_moment_snap)
        val momentSnapCompose: ComposeView = findViewById(R.id.activity_daily_diary_moment_snap_screen)
        momentSnapCompose.setContent {
            RecallifyTheme {

            }
        }
    }
}