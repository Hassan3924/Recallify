package com.example.recallify.view.ui.feature.application.tbi_applications.sidequest

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
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.DailyDiaryActivity
import com.example.recallify.view.ui.feature.application.tbi_applications.dashboard.DashboardActivity
import com.example.recallify.view.ui.feature.application.tbi_applications.tbimainsettings.MainSettingsTBI
import com.example.recallify.view.ui.feature.application.tbi_applications.thinkfast.ThinkFastActivity
import com.example.recallify.view.ui.resource.controller.BottomBarFiller
import com.example.recallify.view.ui.theme.CommonColor
import com.example.recallify.view.ui.theme.RecallifyTheme
import com.google.android.material.bottomnavigation.BottomNavigationView

class SideQuestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_side_quest)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.bottom_side_quest

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> {
                    startActivity(Intent(applicationContext, DashboardActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
                R.id.bottom_daily_diary -> {
                    startActivity(Intent(applicationContext, DailyDiaryActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
                R.id.bottom_side_quest -> true
                R.id.bottom_think_fast -> {
                    startActivity(Intent(applicationContext, ThinkFastActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
                R.id.bottom_accounts -> {
                    startActivity(Intent(applicationContext, MainSettingsTBI::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
                else -> false
            }
        }

        val sideQuestCompose: ComposeView = findViewById(R.id.activity_side_quest_screen)
        sideQuestCompose.setContent {
            val context = LocalContext.current
            RecallifyTheme {
                SideQuestScreen(
                    playGame = {
                        val intent = Intent(context, SideQuestQuizActivity::class.java)
                        startActivity(intent)
                        finish() //added by rb
                    },
                    viewScore = {
                        val intent = Intent(context, SideQuestProgressActivity::class.java)
                        startActivity(intent)
                        finish() //added by rb
                    },
                    viewAnalysis = {
                        val intent = Intent(context, SelectDateAnalyzeSideQuestprogress::class.java)
                        startActivity(intent)
                        finish() //added by rb
                    }
                )
            }
        }
    }


    @Composable
    private fun SideQuestScreen(
        playGame: () -> Unit,
        viewScore: () -> Unit,
        viewAnalysis: () -> Unit,
    ) {
        Scaffold(
            bottomBar = { BottomBarFiller() },
            topBar = { SideQuestTopBar() },
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
                                stringResource(id = R.string.side_quest_introduction),
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .clickable(onClick = playGame),
                        shape = RoundedCornerShape(8.dp),
                        backgroundColor = MaterialTheme.colors.background,
                        elevation = 1.dp,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Tap to play game",
                                    style = MaterialTheme.typography.button.copy(
                                        fontSize = 16.sp
                                    ),
                                    modifier = Modifier.padding(vertical = 16.dp)
                                )
                                Icon(
                                    painter = painterResource(id = R.drawable.round_arrow_forward_24),
                                    contentDescription = "let's play thinkfast",
                                    modifier = Modifier
                                        .size(32.dp)

                                )
                            }
                        }
                    }
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

    @Composable
    private fun SideQuestTopBar() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
//                .padding(horizontal = 16.dp)
                .padding(top = 4.dp)
//                .clip(shape = RoundedCornerShape(26.dp))
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Side Quest",
                style = MaterialTheme.typography.h6

            )
        }
    }
}