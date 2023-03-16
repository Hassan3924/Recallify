package com.example.recallify.view.ui.feature.guradian_application.guardiandashboard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import com.example.recallify.R
import com.example.recallify.view.ui.feature.guradian_application.guardian_account.GuardianAccountsActivity
import com.example.recallify.view.ui.feature.guradian_application.guardiandailydiary.GuardianDailyDairyActivity
import com.example.recallify.view.ui.feature.guradian_application.guardiansidequest.GuardianSideQuestActivity
import com.example.recallify.view.ui.feature.guradian_application.guardianthinkfast.GuardianThinkFastActivity
import com.example.recallify.view.ui.feature.security.signin.LoginActivity
import com.example.recallify.view.ui.resource.controller.BottomBarFiller
import com.example.recallify.view.ui.theme.RecallifyTheme
import com.example.speech_to_text_jetpack.navigation.AudioScreens
import com.google.android.material.bottomnavigation.BottomNavigationView

class GuardiansDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dashboard)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.bottom_home

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> true
                R.id.bottom_daily_diary -> {
                    startActivity(Intent(applicationContext, GuardianDailyDairyActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
                R.id.bottom_side_quest -> {
                    startActivity(Intent(applicationContext, GuardianSideQuestActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
                R.id.bottom_think_fast -> {
                    startActivity(Intent(applicationContext, GuardianThinkFastActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
                R.id.bottom_accounts -> {
                    startActivity(Intent(applicationContext, GuardianAccountsActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
                else -> false
            }
        }


        val dashBoardCompose: ComposeView = findViewById(R.id.activity_dash_board_screen)
        dashBoardCompose.setContent {
            RecallifyTheme {
                DashBoardScreen()
            }
        }
    }

//    override fun onBackPressed() {
//
//        val sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
//        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)
//
//        if (isLoggedIn) {
//            // User is logged in, don't allow them to go back to the login screen
//            super.onBackPressedDispatcher
//
//        } else {
//            // User is not logged in, allow them to go back to the login screen
//            Toast.makeText(this, "Please log in to continue", Toast.LENGTH_SHORT).show()
//        }
//    }

    @Composable
    fun DashBoardScreen() {

        Scaffold(
            bottomBar = { BottomBarFiller() },
            backgroundColor = MaterialTheme.colors.surface
        ) { paddingValue ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValue)
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .padding(horizontal = 16.dp)
                        .padding(4.dp)
                ) {

                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "This is the Guardian Dashboard")
                    }
                }
            }
        }
    }
}