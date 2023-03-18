package com.example.recallify.view.ui.feature.application.tbi_applications.sidequest

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.compose.ui.platform.ComposeView
import com.example.recallify.R
import com.example.recallify.view.common.components.LoadingAnimation
import com.example.recallify.view.ui.theme.RecallifyTheme

class SideQuestLoadingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_side_quest_loading)
        val sidequestLoadingCompose: ComposeView = findViewById(R.id.activity_side_quest_loading_screen)
        sidequestLoadingCompose.setContent {
            RecallifyTheme {
                LoadingAnimation(
                    loadingText = "Hang in tight\nfetching you logs and activities"
                )
            }
        }
    }
}