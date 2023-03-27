package com.example.recallify.view.ui.feature.guradian_application.guardianthinkfast

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recallify.R
import com.example.recallify.view.ui.feature.guradian_application.guardiandailydiary.GuardianDailyDairyActivity
import com.example.recallify.view.ui.feature.guradian_application.guardiandashboard.GuardiansDashboardActivity
import com.example.recallify.view.ui.feature.guradian_application.guardiansidequest.GuardianSideQuestActivity
import com.example.recallify.view.ui.feature.guradian_application.mainsettingpages.GuardianMainSettings
import com.example.recallify.view.ui.resource.controller.BottomBarFiller
import com.example.recallify.view.ui.theme.CommonColor
import com.example.recallify.view.ui.theme.RecallifyTheme
import com.google.android.material.bottomnavigation.BottomNavigationView

class GuardianThinkFastActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_think_fast)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.bottom_think_fast

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> {
                    startActivity(Intent(applicationContext, GuardiansDashboardActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
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
                R.id.bottom_think_fast -> true
                R.id.bottom_accounts -> {
                    startActivity(Intent(applicationContext, GuardianMainSettings::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
                else -> false
            }
        }

        val thinkfastCompose: ComposeView = findViewById(R.id.activity_think_fast_screen)
        thinkfastCompose.setContent {
            RecallifyTheme {
                val context = LocalContext.current
                ThinkFastScreen(
//                    playGame = {
//                        context.startActivity(Intent(context, ThinkfastRulesActivity::class.java))
//                    },
                    viewScore = {
                        context.startActivity(Intent(context, GuardianSelectDateScoreThinkFast::class.java))
                        finish()
                    },
                    viewAnalysis = {
                        context.startActivity(Intent(context, GuardianSelectDateAnalyzeProgressThinkFast::class.java))
                        finish()
                    }
                )
            }
        }
    }

    @Composable
    private fun ThinkFastScreen(
//        playGame: () -> Unit,
        viewScore: () -> Unit,
        viewAnalysis: () -> Unit,
    ) {
        Scaffold(
            bottomBar = { BottomBarFiller() },
            topBar = { ThinkFastTopBar() },
            backgroundColor = MaterialTheme.colors.surface
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Card(
                        backgroundColor = Color.Transparent,
                        elevation = 0.dp,
                    ) {
                        Column {
                            Text(
                                text = "Welcome",
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .padding(top = 16.dp, bottom = 8.dp),
                                style = MaterialTheme.typography.h3.copy(
                                    fontWeight = FontWeight.ExtraBold
                                )
                            )
                            Text(
                                stringResource(R.string.think_fast_introduction),
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .padding(bottom = 16.dp),
                                style = MaterialTheme.typography.subtitle2.copy(
                                    color = Color.Gray
                                )
                            )
                        }
                    }
                }
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally) {

                    Row(modifier = Modifier.fillMaxWidth()) {
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 16.dp)
                                .padding(end = 8.dp),
                            shape = RoundedCornerShape(8.dp),
                            backgroundColor = MaterialTheme.colors.background,
                            elevation = 1.dp,
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable(onClick = viewScore)
                            ) {
                                Text(
                                    "View\nResults",
                                    style = MaterialTheme.typography.button.copy(
                                        fontSize = 16.sp
                                    ),
                                    modifier = Modifier.padding(16.dp)
                                )
                                Icon(
                                    painter = painterResource(id = R.drawable.track_progress),
                                    contentDescription = "progress",
                                )
                            }
                        }
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 16.dp)
                                .padding(start = 8.dp),
                            shape = RoundedCornerShape(8.dp),
                            backgroundColor = CommonColor,
                            elevation = 1.dp,
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable(onClick = viewAnalysis)
                            ) {
                                Text(
                                    "View\nAnalysis",
                                    style = MaterialTheme.typography.button.copy(
                                        fontSize = 16.sp,
                                        color = MaterialTheme.colors.onBackground
                                    ),
                                    modifier = Modifier.padding(16.dp)
                                )
                                Icon(
                                    painter = painterResource(id = R.drawable.track_analysis),
                                    contentDescription = "analysis",
                                    tint = MaterialTheme.colors.onBackground
                                )
                            }
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        shape = RoundedCornerShape(8.dp),
                        backgroundColor = MaterialTheme.colors.background,
                        elevation = 1.dp,
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "💡 The word for the day is [Recall], to remember an even or action over a period of time.")
                        }
                    }
                    
                }

                }
            }
        }
    }

    @Composable
    private fun ThinkFastTopBar() {
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
                text = "Think Fast",
                style = MaterialTheme.typography.body1.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
