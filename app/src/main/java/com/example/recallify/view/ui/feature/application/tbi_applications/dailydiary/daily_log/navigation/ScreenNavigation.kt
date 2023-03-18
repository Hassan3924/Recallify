package com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_log.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_log.screens.AudioLogScreen
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_log.screens.MainScreen
import com.example.speech_to_text_jetpack.navigation.AudioScreens

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AudioVlogNavigation() {

    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AudioScreens.HomeScreen.name
    ) {

        composable(AudioScreens.HomeScreen.name) {
            MainScreen(navController = navController)
        }

        composable(AudioScreens.AudioLogScreen.name) {

            AudioLogScreen(navController = navController)

        }

    }

}