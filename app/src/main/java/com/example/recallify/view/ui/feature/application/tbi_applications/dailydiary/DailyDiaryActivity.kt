package com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.recallify.R
import com.example.recallify.view.common.components.DiaryTopAppBar
import com.example.recallify.view.common.components.TabDiary
import com.example.recallify.view.common.components.TabPage
import com.example.recallify.view.ui.feature.application.dailydiary.conversationSummary.SummarizeConversation
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_activity.DailyActivity
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_log.DailyLogActivity
import com.example.recallify.view.ui.feature.application.tbi_applications.dashboard.DashboardActivity
import com.example.recallify.view.ui.feature.application.tbi_applications.sidequest.SideQuestActivity
import com.example.recallify.view.ui.feature.application.tbi_applications.tbimainsettings.MainSettingsTBI
import com.example.recallify.view.ui.feature.application.tbi_applications.thinkfast.ThinkFastActivity
import com.example.recallify.view.ui.resource.controller.BottomBarFiller
import com.example.recallify.view.ui.theme.RecallifyTheme
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class DailyDiaryActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
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
                    startActivity(Intent(applicationContext, MainSettingsTBI::class.java))
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

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun DailyDiaryScreen() {

        val state = rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            skipHalfExpanded = true
        )
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        var tabPage by remember { mutableStateOf(TabPage.Activity) }

        val database = Firebase.database
        val auth = Firebase.auth
        val currentUser = auth.currentUser?.uid!!
//        val ref = database.getReference("users").child(currentUser).child("conversation-summary").child(getCurrentDate())

        val children = remember { mutableStateListOf<DataSnapshot>() }

        var isLoading by remember { mutableStateOf(true) }

        var selectedDate by remember { mutableStateOf(LocalDate.now()) }


//        Log.d("DatePicked", "$selectedDate")
//        Log.d("Children", children.joinToString())

        LaunchedEffect(selectedDate) {

            val ref = database.getReference("users").child(currentUser).child("conversation-summary").child(selectedDate.toString())

            ref.addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    Log.d("Snapshot", snapshot.toString()) // add this line

                    children.clear()

                    snapshot.children.forEach { child ->
                        children.add(child)
                    }

                    isLoading = false

                }

                override fun onCancelled(error: DatabaseError) {
                    isLoading = false
                }
            })
        }


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
                        pickDate(context = context) {
                            selectedDate = it.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

                        }
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
                                    modifier = Modifier.padding(0.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.padding(vertical = 6.dp))

                        ActionSheetItem(
                            icon = R.drawable.daily_activity,
                            text = "Summarised Text",
                            onStart = {
                                val intent =
                                    Intent(this@DailyDiaryActivity, SummarizeConversation::class.java)
                                startActivity(intent)
                            }
                        )

                        Spacer(modifier = Modifier.padding(vertical = 6.dp))
                        ActionSheetItem(
                            icon = R.drawable.daily_activity,
                            text = "Daily activity",
                            onStart = {
                                val intent =
                                    Intent(this@DailyDiaryActivity, DailyActivity::class.java)
                                startActivity(intent)
                            }
                        )
                        Spacer(modifier = Modifier.padding(vertical = 5.dp))
                        ActionSheetItem(
                            icon = R.drawable.daily_log,
                            text = "Daily log",
                            onStart = {
                                val intent =
                                    Intent(this@DailyDiaryActivity, DailyLogActivity::class.java)
                                startActivity(intent)
                            }
                        )
                        Spacer(modifier = Modifier.padding(vertical = 5.dp))
                        ActionSheetItem(
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
                                            state.animateTo(
                                                ModalBottomSheetValue.Hidden,
                                                tween(400)
                                            )
                                        }
                                    }
                                )

                            }
                            1 -> {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(top = 8.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    when {

                                        children.isNullOrEmpty() -> {

                                            Text(
                                                text = "No data available for ${selectedDate.format(DateTimeFormatter.ISO_DATE)}",
                                                modifier = Modifier.padding(16.dp),
                                                textAlign = TextAlign.Center
                                            )
                                        }

                                        else -> {
                                            LazyColumn {

                                                items(children) { child ->
                                                    Card(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(16.dp),
                                                        elevation = 8.dp,
                                                    ) {
                                                        Text(
                                                            text = child.value.toString(),
                                                            modifier = Modifier.padding(16.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }




                                BackHandler(
                                    enabled = (state.currentValue == ModalBottomSheetValue.HalfExpanded ||
                                            state.currentValue == ModalBottomSheetValue.Expanded),
                                    onBack = {
                                        scope.launch {
                                            state.animateTo(
                                                ModalBottomSheetValue.Hidden,
                                                tween(300)
                                            )
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

    fun pickDate(context: Context, onDateSelected: (Date) -> Unit) {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                onDateSelected(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    @Composable
    fun ActionSheetItem(icon: Int, text: String, onStart: () -> Unit) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .clickable {
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
            Text(
                text,
                style = MaterialTheme.typography.button,
                color = MaterialTheme.colors.onSurface
            )
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



    fun getCurrentDate(): String {

        val date = Date().time
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(date)

    }

}