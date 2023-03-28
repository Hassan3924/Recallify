package com.example.recallify.view.ui.feature.guradian_application.guardiandailydiary

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.recallify.R
import com.example.recallify.view.common.components.DiaryTopAppBarGuardian
import com.example.recallify.view.common.components.TabDiary
import com.example.recallify.view.common.components.TabPage
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.Information
import com.example.recallify.view.ui.feature.guradian_application.guardiandashboard.GuardiansDashboardActivity
import com.example.recallify.view.ui.feature.guradian_application.guardiansidequest.GuardianSideQuestActivity
import com.example.recallify.view.ui.feature.guradian_application.guardianthinkfast.GuardianThinkFastActivity
import com.example.recallify.view.ui.feature.guradian_application.mainsettingpages.GuardianMainSettings
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
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class GuardianDailyDairyActivity : AppCompatActivity() {

    private var tbiUID = mutableStateOf("")

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

//        var tbiEmail: String = ""
//
//        val tbiGULinkID: String = ""

        val tbiEmail = remember { mutableStateOf("") }

        val childrenOfActivities = remember { mutableStateListOf<Information>() }

        var isActivitiesLoading by remember { mutableStateOf(true) }


        val lazyColumnState = rememberLazyListState()

        LaunchedEffect(selectedDate, tbiUID.value) {

            val tbiEmailRef = database
                .getReference("users")
                .child(currentUser)
                .child("profile")
                .child("TBI Email")

            val conversationSum = database
                .getReference("users")
                .child(tbiUID.value)
                .child("conversation-summary")
                .child(selectedDate.toString())

            val tbiActivityRef = database.getReference("users")
                .child(tbiUID.value)
                .child("dailyDairyDummy")
                .child(selectedDate.toString())

            val guardianLinkRef = database
                .getReference("users")
                .child("GuardiansLinkTable")
                .child(currentUser)
                .child("TBI_ID")

            /*
            * TBI Email Reference!
            * */
            tbiEmailRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    tbiEmail.value = snapshot.getValue(String::class.java).toString()
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
                    tbiUID.value = snapshot.getValue(String::class.java).toString()
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
                    children.clear()
                    snapshot.children.forEach { child ->
                        children.add(child)
                        Log.d("Child data", "Added child: ${child.value}")

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
            tbiActivityRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    childrenOfActivities.clear()
                    for (DataSnap in snapshot.children) {
                        val selectedActivities = DataSnap.getValue(Information::class.java)
                        if (selectedActivities != null) {
                            childrenOfActivities.add(0, selectedActivities)
                        }
                    }
                    isActivitiesLoading = false
                }

                override fun onCancelled(error: DatabaseError) {
                    isActivitiesLoading = false
                }
            })

        }

        Scaffold(
            scaffoldState = rememberScaffoldState(),
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
                            when {
                                childrenOfActivities.isEmpty() -> {
                                    Box(
                                        Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "No data available for\n ${
                                                selectedDate.format(
                                                    DateTimeFormatter.ISO_DATE
                                                )
                                            }",
                                            modifier = Modifier.padding(16.dp),
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                                childrenOfActivities.isNotEmpty() -> {
                                    LazyColumn(
                                        state = lazyColumnState,
                                        contentPadding = PaddingValues(
                                            vertical = 16.dp
                                        ),
                                        verticalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        items(childrenOfActivities) { activity ->
                                            ActivityItem(activity = activity)
                                        }
                                    }
                                }
                                else -> {
                                    Box(
                                        Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "Data could not be fetched from Network!",
                                            modifier = Modifier.padding(16.dp),
                                            textAlign = TextAlign.Center
                                        )
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

                                    children.isEmpty() -> {

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

    @Composable
    private fun ActivityItem(activity: Information) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            backgroundColor = MaterialTheme.colors.background
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = rememberAsyncImagePainter(
                            activity.imageLink
                        ),
                        contentDescription = "Activity-Image",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(4.dp)
                            .clip(RoundedCornerShape(6.dp))
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 4.dp
                        )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = activity.date.toString(),
                            style = MaterialTheme.typography.caption.copy(
                                color = Color.LightGray,
                                fontSize = 14.sp
                            )
                        )
                        Spacer(
                            modifier = Modifier.padding(
                                horizontal = 6.dp
                            )
                        )
                        Text(
                            text = activity.time.toString(),
                            style = MaterialTheme.typography.caption.copy(
                                color = Color.LightGray,
                                fontSize = 14.sp
                            )
                        )
                    }
                    Text(
                        text = activity.locationName.toString(),
                        style = MaterialTheme.typography.h6.copy(

                        )
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(
                        text = activity.locationAddress.toString(),
                        style = MaterialTheme.typography.caption.copy(
                            color = Color.Gray,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }

    private fun pickDate(context: Context, onDateSelected: (Date) -> Unit) {
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
}