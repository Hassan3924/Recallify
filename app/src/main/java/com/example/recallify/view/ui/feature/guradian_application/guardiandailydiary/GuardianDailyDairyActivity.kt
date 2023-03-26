package com.example.recallify.view.ui.feature.guradian_application.guardiandailydiary

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
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
import coil.memory.MemoryCache
import com.example.recallify.R
import com.example.recallify.view.common.components.DiaryTopAppBar
import com.example.recallify.view.common.components.DiaryTopAppBarGuardian
import com.example.recallify.view.common.components.TabDiary
import com.example.recallify.view.common.components.TabPage
import com.example.recallify.view.ui.feature.application.dailydiary.conversationSummary.SummarizeConversation
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.Information
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_activity.DailyActivity
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_log.DailyLogActivity
import com.example.recallify.view.ui.feature.application.tbi_applications.dashboard.DashboardActivity
import com.example.recallify.view.ui.feature.application.tbi_applications.sidequest.SideQuestActivity
import com.example.recallify.view.ui.feature.application.tbi_applications.tbimainsettings.MainSettingsTBI
import com.example.recallify.view.ui.feature.application.tbi_applications.thinkfast.ThinkFastActivity
import com.example.recallify.view.ui.feature.guradian_application.mainsettingpages.GuardianMainSettings
import com.example.recallify.view.ui.feature.guradian_application.guardiandashboard.GuardiansDashboardActivity
import com.example.recallify.view.ui.feature.guradian_application.guardiansidequest.GuardianSideQuestActivity
import com.example.recallify.view.ui.feature.guradian_application.guardianthinkfast.GuardianThinkFastActivity
import com.example.recallify.view.ui.resource.controller.BottomBarFiller
import com.example.recallify.view.ui.theme.RecallifyTheme
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class GuardianDailyDairyActivity : AppCompatActivity() {

    private var tbi_uid: String = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_diary)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.bottom_daily_diary

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> {
                    startActivity(
                        Intent(
                            applicationContext,
                            GuardiansDashboardActivity::class.java
                        )
                    )
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
                R.id.bottom_daily_diary -> true
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
                    startActivity(Intent(applicationContext, GuardianMainSettings::class.java))
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
                GuardianDailyDiaryScreen()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun GuardianDailyDiaryScreen() {

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

        var tbiEmail: String = ""

        val tbiGULinkID: String = ""

        val childrenOfActivities = remember {
            mutableStateListOf<Information>()
        }

        var isActivitiesLoading by remember { mutableStateOf(true) }

        LaunchedEffect(selectedDate, tbiEmail, tbiGULinkID) {

            val tbiEmailRef = database.getReference("users")
                .child(currentUser)
                .child("profile")
                .child("TBI Email")

            val conversationSum = database.getReference("users")
                .child(tbi_uid)
                .child("conversation-summary")
                .child(selectedDate.toString())

            val tbiActivityRef = database.getReference("users")
                .child(tbi_uid)
                .child("dailyDairyDummy")
                .child(selectedDate.toString())

            val guardianLinkRef = database.getReference("users")
                .child("GuardiansLinkTable")
                .child(currentUser)
                .child("TBI_ID")

//            Log.d("GuardianLinkRef2", tbi_uid)

            /*
            * TBI Email Reference!
            * */
            tbiEmailRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    tbiEmail = snapshot.getValue(String::class.java).toString()
                    Log.d("GuardianLink", tbiEmail)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Error retrieving data", error.toException())
                }
            })


            /*
            * Guardian Link Reference
            * */
            guardianLinkRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    tbi_uid = snapshot.getValue(String::class.java).toString()
                    Log.d("GuardianLinkRef", tbiGULinkID)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })

            /*
            * Conversation Summary!
            * */
            conversationSum.addValueEventListener(object : ValueEventListener {
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

            /*
            * TBI Daily Activities!
            * */
        }

        Scaffold(
            bottomBar = { BottomBarFiller() },
            topBar = {
                DiaryTopAppBarGuardian(

                    clickFilter = {
                        pickDate(context = context) {
                            selectedDate =
                                it.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

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
                                            text = "No data available for ${
                                                selectedDate.format(
                                                    DateTimeFormatter.ISO_DATE
                                                )
                                            }",
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