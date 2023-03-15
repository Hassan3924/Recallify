package com.example.recallify.view.ui.feature.application.dailydiary

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.recallify.R
import com.example.recallify.view.common.components.DiaryTopAppBar
import com.example.recallify.view.common.components.TabDiary
import com.example.recallify.view.common.components.TabPage
import com.example.recallify.view.ui.feature.application.accounts.AccountsActivity
import com.example.recallify.view.ui.feature.application.dailydiary.daily_activity.DailyActivity
import com.example.recallify.view.ui.feature.application.dailydiary.daily_log.DailyLogActivity
import com.example.recallify.view.ui.feature.application.dashboard.DashboardActivity
import com.example.recallify.view.ui.feature.application.sidequest.SideQuestActivity
import com.example.recallify.view.ui.feature.application.thinkfast.ThinkFastActivity
import com.example.recallify.view.ui.resource.controller.BottomBarFiller
import com.example.recallify.view.ui.theme.RecallifyTheme
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.launch

class DailyDiaryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_diary)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.bottom_daily_diary

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> {
                    startActivity(Intent(applicationContext, DashboardActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
                R.id.bottom_daily_diary -> true
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
                R.id.bottom_accounts -> {
                    startActivity(Intent(applicationContext, AccountsActivity::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
                else -> false
            }
        }

        val dailyDiaryCompose: ComposeView = findViewById(R.id.activity_daily_diary_screen)
        dailyDiaryCompose.setContent {
            RecallifyTheme {
                DailyDiaryScreen()
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun DailyDiaryScreen() {
        val state = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden, skipHalfExpanded = true)
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        var tabPage by remember { mutableStateOf(TabPage.Activity) }

        Scaffold(
            bottomBar = { BottomBarFiller() },
            topBar = {
                DiaryTopAppBar(
                    clickCreate = {
                        scope.launch {
                            if (state.currentValue == ModalBottomSheetValue.Hidden) {
                                state.animateTo(ModalBottomSheetValue.Expanded, tween(500))
                            } else {
                                state.animateTo(ModalBottomSheetValue.Hidden, tween(500))
                            }
                        }
                    },
                    clickFilter = {
                        Toast.makeText(context, "Filtering...", Toast.LENGTH_SHORT).show()
                    }
                )
            },
            backgroundColor = MaterialTheme.colors.surface
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ModalBottomSheetLayout(
                    sheetContent = {
                        Surface(modifier = Modifier.background(MaterialTheme.colors.primaryVariant)) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                                    .background(MaterialTheme.colors.secondary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "+ Create",
                                    style = MaterialTheme.typography.h6,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.padding(vertical = 6.dp))
                        ActionSheetItem(
                            context = context,
                            icon = R.drawable.daily_activity,
                            text = "Daily activity",
                            onStart = {
                                val intent = Intent(this@DailyDiaryActivity, DailyActivity::class.java)
                                startActivity(intent)
                            }
                        )
                        Spacer(modifier = Modifier.padding(vertical = 5.dp))
                        ActionSheetItem(
                            context = context,
                            icon = R.drawable.daily_log,
                            text = "Daily log",
                            onStart = {
                                val intent = Intent(this@DailyDiaryActivity, DailyLogActivity::class.java)
                                startActivity(intent)
                            }
                        )
                        Spacer(modifier = Modifier.padding(vertical = 5.dp))
                        ActionSheetItem(
                            context = context,
                            icon = R.drawable.moment_snap,
                            text = "Moment snap",
                            onStart = {

                            }
                        )
                    },
                    sheetBackgroundColor = MaterialTheme.colors.background,
                    sheetElevation = 5.dp,
                    sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    sheetState = state,

                ) {
                    Column {
                        TabDiary(selectTabIndex = tabPage.ordinal, onSelectTab = { tabPage = it })
                        when (tabPage.ordinal) {
                            0 -> {
                                ItemCount(text = "10")
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(top = 8.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {

                                    Text("Daily Activities", color = MaterialTheme.colors.onSurface)
                                    // fixme: add composable list here
                                }

                                BackHandler(
                                    enabled = (state.currentValue == ModalBottomSheetValue.HalfExpanded ||
                                            state.currentValue == ModalBottomSheetValue.Expanded),
                                    onBack = {
                                        scope.launch {
                                            state.animateTo(ModalBottomSheetValue.Hidden, tween(400))
                                        }
                                    }
                                )

                            }
                            1 -> {
                                ItemCount(text = "20")
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(top = 8.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {

                                    Text("Daily Activity", color = MaterialTheme.colors.onSurface)
                                    // fixme: add composable list here
                                }

                                BackHandler(
                                    enabled = (state.currentValue == ModalBottomSheetValue.HalfExpanded ||
                                            state.currentValue == ModalBottomSheetValue.Expanded),
                                    onBack = {
                                        scope.launch {
                                            state.animateTo(ModalBottomSheetValue.Hidden, tween(300))
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun ActionSheetItem(context: Context, icon: Int, text: String, onStart: () -> Unit) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .clickable {
                    Toast
                        .makeText(context, "Channel: $text", Toast.LENGTH_SHORT)
                        .show()
                    onStart()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = text,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colors.onSurface
            )
            Spacer(modifier = Modifier.padding(horizontal = 10.dp))
            Text(text, style = MaterialTheme.typography.button, color = MaterialTheme.colors.onSurface)
        }
    }


    @Composable
    fun ItemCount(text: String) {
        Text(
            "count: $text",
            style = MaterialTheme.typography.caption,
            color = if (isSystemInDarkTheme()) Color.LightGray else Color.Gray,
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        )
    }

}