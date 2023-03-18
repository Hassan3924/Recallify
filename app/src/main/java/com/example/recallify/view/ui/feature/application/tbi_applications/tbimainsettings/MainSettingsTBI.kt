package com.example.recallify.view.ui.feature.application.tbi_applications.tbimainsettings

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.recallify.R
import com.example.recallify.view.ui.feature.application.tbi_applications.accounts.AccountsActivity
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_activity.DailyActivity
import com.example.recallify.view.ui.feature.application.tbi_applications.dashboard.DashboardActivity
import com.example.recallify.view.ui.feature.application.tbi_applications.sidequest.SideQuestActivity
import com.example.recallify.view.ui.feature.application.tbi_applications.thinkfast.ThinkFastActivity
import com.example.recallify.view.ui.feature.guradian_application.guardiandailydiary.GuardianDailyDairyActivity
import com.example.recallify.view.ui.feature.guradian_application.guardiandashboard.GuardiansDashboardActivity
import com.example.recallify.view.ui.feature.guradian_application.guardiansidequest.GuardianSideQuestActivity
import com.example.recallify.view.ui.feature.guradian_application.guardianthinkfast.GuardianThinkFastActivity
import com.example.recallify.view.ui.feature.guradian_application.mainsettingpages.HelpAndSupport
import com.example.recallify.view.ui.feature.guradian_application.mainsettingpages.PrivacyAndSecurity
import com.example.recallify.view.ui.feature.guradian_application.mainsettingpages.TermsAndConditions
import com.example.recallify.view.ui.resource.controller.BottomBarFiller
import com.example.recallify.view.ui.theme.RecallifyTheme
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class MainSettingsTBI : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main_settings)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.bottom_accounts

        bottomNavigationView.setOnItemSelectedListener { item ->

            when (item.itemId) {
                R.id.bottom_home -> {
                    startActivity(Intent(applicationContext, DashboardActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }

                R.id.bottom_daily_diary -> {
                    startActivity(Intent(applicationContext, DailyActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }

                R.id.bottom_side_quest -> {
                    startActivity(Intent(applicationContext, SideQuestActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }

                R.id.bottom_think_fast -> {
                    startActivity(Intent(applicationContext, ThinkFastActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }

                R.id.bottom_accounts -> true

                else -> false
            }
        }

        val guardianMainSettings: ComposeView = findViewById(R.id.activity_main_settings)
        guardianMainSettings.setContent {
            RecallifyTheme {
                MainSettingsScreenTBI()
            }
        }
    }

    @Composable
    fun MainSettingsScreenTBI() {

        val auth: FirebaseAuth = Firebase.auth
        val current = auth.currentUser?.uid!!
        val database = Firebase.database.reference.child("users")
        val user = database.child(current).child("profile").child("firstname")
        val userName = remember { mutableStateOf<String>("") }

        user.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                userName.value = snapshot.value.toString()
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })

        Scaffold(
            topBar = { MainSettingsTopBar() },
            bottomBar = { BottomBarFiller() },
            backgroundColor = MaterialTheme.colors.surface
        ) {paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .padding(top = 4.dp)
                        .padding(bottom = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Card(
                        backgroundColor = Color.Transparent,
                        elevation = 0.dp,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Greetings ${userName.value}!",
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .padding(top = 16.dp, bottom = 8.dp),
                                style = MaterialTheme.typography.h4.copy(
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                        }


                        Column(modifier = Modifier.padding(top = 100.dp, bottom = 10.dp, start = 10.dp, end = 10.dp)) {

                            Card(modifier = Modifier.padding(top = 30.dp),
                                elevation = 5.dp) {
                                Row(
                                    horizontalArrangement = Arrangement.Start,
                                    modifier = Modifier
                                        .clickable(onClick = {
                                            val intent = Intent(
                                                applicationContext,
                                                AccountsActivity::class.java
                                            )
                                            startActivity(intent)
                                            overridePendingTransition(
                                                R.anim.slide_in_right,
                                                R.anim.slide_out_left
                                            )
                                            finish()
                                        })
                                        .padding(horizontal = 10.dp, vertical = 10.dp)
                                        .fillMaxWidth()
                                ) {
                                    Text(text = "Account Setting")
                                }
                            }

                            Card(modifier = Modifier.padding(top = 30.dp),
                                elevation = 5.dp) {
                                Row(
                                    horizontalArrangement = Arrangement.Start,
                                    modifier = Modifier
                                        .clickable(onClick = {
                                            val intent = Intent(
                                                applicationContext,
                                                PrivacyAndSecurityTBI::class.java
                                            )
                                            startActivity(intent)
                                            overridePendingTransition(
                                                R.anim.slide_in_right,
                                                R.anim.slide_out_left
                                            )
                                            finish()
                                        })
                                        .padding(horizontal = 10.dp, vertical = 10.dp)
                                        .fillMaxWidth()
                                ) {
                                    Text(text = "Privacy & Security")
                                }
                            }


                            Card(modifier = Modifier.padding(top = 30.dp),
                                elevation = 5.dp) {
                                Row(
                                    horizontalArrangement = Arrangement.Start,
                                    modifier = Modifier
                                        .clickable(onClick = {
                                            val intent = Intent(
                                                applicationContext,
                                                TermsAndConditionsTBI::class.java
                                            )
                                            startActivity(intent)
                                            overridePendingTransition(
                                                R.anim.slide_in_right,
                                                R.anim.slide_out_left
                                            )
                                            finish()
                                        })
                                        .padding(horizontal = 10.dp, vertical = 10.dp)
                                        .fillMaxWidth()
                                ) {
                                    Text(text = "Terms & Conditions")
                                }
                            }

                            Card(modifier = Modifier.padding(top = 30.dp),
                                elevation = 5.dp) {
                                Row(
                                    horizontalArrangement = Arrangement.Start,
                                    modifier = Modifier
                                        .clickable(onClick = {
                                            val intent = Intent(
                                                applicationContext,
                                                HelpAndSupportTBI::class.java
                                            )
                                            startActivity(intent)
                                            overridePendingTransition(
                                                R.anim.slide_in_right,
                                                R.anim.slide_out_left
                                            )
                                            finish()
                                        })
                                        .padding(horizontal = 10.dp, vertical = 10.dp)
                                        .fillMaxWidth()
                                ) {
                                    Text(text = "Help & Support")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun MainSettingsTopBar() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp)
                .padding(top = 4.dp)
                .clip(shape = RoundedCornerShape(26.dp))
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Main Settings",
                style = MaterialTheme.typography.body1.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}