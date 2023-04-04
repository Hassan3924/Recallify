package com.example.recallify.view.ui.feature.application.tbi_applications.dashboard

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recallify.R
import com.example.recallify.view.common.components.DashBoardTopAppBar
import com.example.recallify.view.common.function.getCurrentDate
import com.example.recallify.view.common.resources.Constants.ANALYZE_CORRECT_VALUES_PATH
import com.example.recallify.view.common.resources.Constants.ANALYZE_ROOT_PATH
import com.example.recallify.view.common.resources.Constants.DAILY_DAIRY_ACTIVITY
import com.example.recallify.view.common.resources.Constants.DATE_PATH
import com.example.recallify.view.common.resources.Constants.DATE_PATTERN
import com.example.recallify.view.common.resources.Constants.LOCATION_ADDRESS_PATH
import com.example.recallify.view.common.resources.Constants.LOCATION_PATH
import com.example.recallify.view.common.resources.Constants.SIDE_QUEST_SCORE_PATH
import com.example.recallify.view.common.resources.Constants.USER_ROOT_PATH
import com.example.recallify.view.common.resources.Constants.TIME_PATH
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.DailyDiaryActivity
import com.example.recallify.view.ui.feature.application.tbi_applications.sidequest.SideQuestActivity
import com.example.recallify.view.ui.feature.application.tbi_applications.tbimainsettings.MainSettingsTBI
import com.example.recallify.view.ui.feature.application.tbi_applications.thinkfast.ThinkFastActivity
import com.example.recallify.view.ui.resource.controller.BottomBarFiller
import com.example.recallify.view.ui.theme.RecallifyTheme
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * The dashboard, one of the core components of the application that relays the latest activity and
 * the Thinkfast and Sidequest progress report. Note: **This is the view of the TBI user**
 *
 * @author Hassan Enoabasi Ridinbal
 * */
open class DashboardActivity : AppCompatActivity() {
    /**
     * Firebase database reference to recallify's firebase console.
     *
     * @see FirebaseDatabase
     * @author Hassan Enoabasi Ridinbal
     * */
    val database = FirebaseDatabase.getInstance()

    /**
     * Firebase authentication reference to recallify's firebase console.
     *
     * @see FirebaseAuth
     * @author Hassan Enoabasi Ridinbal
     * */
    val auth = FirebaseAuth.getInstance()

    /**
     * The current user in the application.
     *
     * @author Hassan Enoabasi Ridinbal
     * */
    val user = auth.currentUser

    /**
     * The user ID of the current user. The value has been null and safety checked.
     *
     * @author Hassan Enoabasi Ridinbal
     * */
    private val userID = auth.currentUser?.uid!!

    /**
     * The current local date in its raw state.
     *
     * @author Hassan Enoabasi Ridinbal
     * */
    @RequiresApi(Build.VERSION_CODES.O)
    private val current = LocalDateTime.now()

    /**
     * The format of the Recallify date pattern to be used as the current date.
     *
     * @author Hassan Enoabasi Ridinbal
     * */
    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ofPattern(DATE_PATTERN)

    /**
     * The formatted date of the Recallify date pattern.
     *
     * @author Hassan Enoabasi Ridinbal
     * */
    @RequiresApi(Build.VERSION_CODES.O)
    private val formatted = current.format(formatter)

    /**
     * The finalized current date of the users based on the Recallify date pattern.
     *
     * @author Hassan Enoabasi Ridinbal
     * */
    @RequiresApi(Build.VERSION_CODES.O)
    val currentDate: String = formatted.toString()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Conversation summary service provider
//        startService(
//            Intent(
//                this@DashboardActivity,
//                NotificationService::class.java
//            )
//        )

        /**
         * The bottom bar navigation controller of the application.
         *
         * @author enoabasi
         * */
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.bottom_home
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> true
                R.id.bottom_daily_diary -> {
                    startActivity(Intent(applicationContext, DailyDiaryActivity::class.java))
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
                R.id.bottom_accounts -> {
                    startActivity(Intent(applicationContext, MainSettingsTBI::class.java))
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    finish()
                    true
                }
                else -> false
            }
        }

        /**
         * The composable view of the dashboard XML layout.
         * @author Enoabasi
         * */
        val dashBoardCompose: ComposeView = findViewById(R.id.activity_dash_board_screen)
        dashBoardCompose.setContent {
            RecallifyTheme {
                DashBoardScreen()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun DashBoardScreen() {

        val scrollState = rememberScrollState()

        val isLoading = remember {
            mutableStateOf(true)
        }

        val selectedBar by remember {
            mutableStateOf(-1)
        }

        val chartData = remember {
            mutableStateOf(emptyList<BarCharInput>())
        }

        val chartDataSQ = remember {
            mutableStateOf(emptyList<BarCharInputSQ>())
        }

        var isActivityLoading by remember {
            mutableStateOf(true)
        }

        val date = remember {
            mutableStateOf("")
        }

        val time = remember {
            mutableStateOf("")
        }

        val locationName = remember {
            mutableStateOf("")
        }

        val locationAddress = remember {
            mutableStateOf("")
        }

        LaunchedEffect(Unit) {
            firebaseChartData { fetchedData ->
                chartData.value = fetchedData
                isLoading.value = false
            }

            firebaseChartDataSQ { fetchedData ->
                chartDataSQ.value = fetchedData
                isLoading.value = false
            }
        }

        LaunchedEffect(getCurrentDate()) {
            val latestDB = FirebaseDatabase.getInstance().reference
            val latestActivityPath = latestDB.child(USER_ROOT_PATH)
                .child(userID)
                .child(DAILY_DAIRY_ACTIVITY)
                .child(getCurrentDate())

            isActivityLoading = true

            latestActivityPath.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (childSnapshot in snapshot.children) {
                            date.value =
                                childSnapshot.child(DATE_PATH).value.toString()
                            time.value =
                                childSnapshot.child(TIME_PATH).value.toString()
                            locationName.value =
                                childSnapshot.child(LOCATION_PATH).value.toString()
                            locationAddress.value =
                                childSnapshot.child(LOCATION_ADDRESS_PATH).value.toString()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    isActivityLoading = false
                }
            })
        }

        Scaffold(
            bottomBar = { BottomBarFiller() },
            topBar = { DashBoardTopAppBar() },
            backgroundColor = MaterialTheme.colors.surface
        ) { paddingValue ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValue)
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            Modifier
                                .fillMaxSize()
                                .padding(top = 8.dp)
                                .padding(bottom = 20.dp)
                        ) {
                            if (locationName.value.isNotBlank()) {
                                Text(
                                    text = "Latest activity",
                                    style = MaterialTheme.typography.body1.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    elevation = 5.dp,
                                    backgroundColor = MaterialTheme.colors.background
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.SpaceEvenly
                                    ) {
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
                                                    text = date.value,
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
                                                    text = time.value,
                                                    style = MaterialTheme.typography.caption.copy(
                                                        color = Color.LightGray,
                                                        fontSize = 14.sp
                                                    )
                                                )
                                            }
                                            Text(
                                                text = "last seen at",
                                                style = MaterialTheme.typography.caption.copy(
                                                    color = Color.Gray,
                                                    fontSize = 12.sp
                                                )
                                            )
                                            Text(
                                                text = locationName.value,
                                                style = MaterialTheme.typography.h6.copy(

                                                )
                                            )
                                            Spacer(modifier = Modifier.padding(4.dp))
                                            Text(
                                                text = locationAddress.value,
                                                style = MaterialTheme.typography.caption.copy(
                                                    color = Color.Gray,
                                                    fontSize = 12.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp),
                        ) {
                            Text(
                                text = "Think fast progress",
                                style = MaterialTheme.typography.body1.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                modifier = Modifier.padding(bottom = 4.dp)
                            )

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 12.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colors.background),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (isLoading.value) {
                                    CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                                } else {
                                    BarChart(
                                        chartData.value,
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier.padding(vertical = 8.dp),
                            ) {
                                Text(
                                    text = "Side quest progress",
                                    style = MaterialTheme.typography.body1.copy(
                                        fontWeight = FontWeight.Medium
                                    ),
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colors.background),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    if (isLoading.value) {
                                        CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                                    } else {
                                        BarChartSQ(
                                            chartDataSQ.value,
                                            modifier = Modifier.fillMaxWidth(),
                                            selectedBar = selectedBar,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     *  The chart data gotten from the firebase database at the Recallify firebase console.
     *  This chart is the representation for Think Fast Progress report.
     *
     * @param onDataFetched A list of all the valid input data
     * @author Hassan
     * */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun firebaseChartData(onDataFetched: (List<BarCharInput>) -> Unit) {

        /**
         * The reference path to Recallify's firebase analysis table.
         *
         * @author Hassan
         */
        val database =
            Firebase.database.reference
                .child(ANALYZE_ROOT_PATH)
                .child(userID)

        /**
         * Graph colors to be used when displaying a graph.
         *
         * @author Hassan
         */
        val colors = listOf(
            Color.White,
            Color.Gray,
            Color.Yellow,
            Color.Cyan,
            Color.Magenta,
            Color.Blue
        )

        /**
         * The range of records to be shown in the dashboard.
         *
         * @author Hassan
         * */
        val sevenDays = (0..5).map { LocalDate.now().minusDays(it.toLong()) }

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                /**
                 * The bar chart input data.
                 *
                 * @author Hassan
                 * */
                val barCharInputData = sevenDays.mapNotNull { date ->

                    /**
                     * The snapshot object provided by firebase.
                     * @author Hassan
                     * */
                    val dataSnapshot = snapshot.child(date.toString())

                    /**
                     * The total number of correct answers from the analysis table. To be used in
                     * chart report calculations.
                     *
                     * @author Hassan
                     * */
                    val totalCorrect =
                        dataSnapshot
                            .child(ANALYZE_CORRECT_VALUES_PATH)
                            .getValue(Int::class.java) ?: 0

                    BarCharInput(
                        totalCorrect,
                        date.toString(),
                        colors[sevenDays.indexOf(date) % colors.size],
                        date.toString()
                    )
                }

                onDataFetched(barCharInputData)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseChartData-onCancelled", "onCalled called ${error.message}")
            }
        })
    }

    /**
     *  The chart data gotten from the firebase database at the Recallify firebase console.
     *  This chart is the representation for Side quest Progress report.
     *
     * @param onDataFetched A list of all the valid input data
     * @author Hassan
     * */
    @RequiresApi(Build.VERSION_CODES.O)
    fun firebaseChartDataSQ(onDataFetched: (List<BarCharInputSQ>) -> Unit) {
        /**
         * The reference path to Recallify's firebase side quest scores table.
         *
         * @author Hassan
         */
        val database =
            Firebase.database.reference
                .child(USER_ROOT_PATH)
                .child(userID)
                .child(SIDE_QUEST_SCORE_PATH)

        /**
         * Graph colors to be used when displaying a graph.
         *
         * @author Hassan
         */
        val colors = listOf(
            Color.White,
            Color.Gray,
            Color.Yellow,
            Color.Cyan,
            Color.Magenta,
            Color.Blue
        )

        /**
         * The range of records to be shown in the dashboard.
         *
         * @author Hassan
         * */
        val sevenDays = (0..5).map { LocalDate.now().minusDays(it.toLong()) }

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                /**
                 * The bar chart input data.
                 *
                 * @author Hassan
                 * */
                val barCharInputData = sevenDays.map { date ->
                    /**
                     * The snapshot object provided by firebase.
                     * @author Hassan
                     * */
                    val dataSnapshot = snapshot.child(date.toString())

                    /**
                     *
                     * @author Hass
                     * */
                    val games = dataSnapshot.children.toList()

                    /**
                     * The total number of correct answers from the analysis table. To be used in
                     * chart report calculations.
                     *
                     * @author Hassan
                     * */
                    val score =
                        games.firstOrNull()?.child("correct")?.getValue(Int::class.java) ?: 0

                    BarCharInputSQ(
                        value = score,
                        label = date.toString(),
                        color = colors[sevenDays.indexOf(date) % colors.size],
                        date = date.toString() // Pass the date as a property to BarCharInput
                    )
                }.filterNotNull()

                Log.d("FirebaseChartData", "Fetched data: $barCharInputData")

                onDataFetched(barCharInputData)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseChartData-onCancelled", "onCalled called ${error.message}")
            }

        })
    }

    @Composable
    fun BarChart(
        inputList: List<BarCharInput>,
        modifier: Modifier = Modifier,
    ) {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val maxValue by remember {
                mutableStateOf(inputList.maxOfOrNull { it.value } ?: 0)
            }
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                inputList.forEachIndexed { _, input ->
                    Bar(
                        modifier = Modifier,
                        value = input.value,
                        maxValue = maxValue,
                        date = input.date,
                    )
                }
            }
        }
    }

    @Composable
    fun BarChartSQ(
        inputList: List<BarCharInputSQ>,
        modifier: Modifier = Modifier,
        selectedBar: Int,
    ) {

        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            val maxValue = inputList.maxOfOrNull { it.value } ?: 0

            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {


                inputList.forEachIndexed { index, input ->

                    BarSQ(
                        modifier = Modifier,
                        value = input.value,
                        maxValue = maxValue,
                        label = input.label,
                        date = input.date,
                        showDescription = selectedBar == index
                    )
                }
            }
        }
    }

    @Composable
    fun BarSQ(
        modifier: Modifier = Modifier,
        value: Int,
        maxValue: Int,
        label: String,
        date: String,
        showDescription: Boolean
    ) {
        val barWidth = 40.dp
        val minHeight = 16.dp
        val barHeight = maxOf(
            minHeight,
            if (maxValue != 0) 160.dp * (value.toFloat() / maxValue.toFloat()) else 0.dp
        )
        Column(
            modifier = modifier
                .height(220.dp)
                .width(barWidth)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Box(
                modifier = modifier
                    .height(barHeight)
                    .width(barWidth)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = if (value > 0) listOf(
                                MaterialTheme.colors.primary.copy(
                                    alpha = ContentAlpha.medium
                                ),
                                MaterialTheme.colors.primary.copy(
                                    alpha = ContentAlpha.medium
                                )
                            ) else listOf(Color.LightGray, Color.LightGray),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        ),
                        shape = RoundedCornerShape(50.dp)
                    ),
                contentAlignment = Center
            ) {
                Text(
                    text = "$value",
                    style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = date,
                style = MaterialTheme.typography.caption.copy(
                    fontSize = 10.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(barWidth)
                    .padding(top = 5.dp)
            )

            if (showDescription) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.caption,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .width(barWidth)
                        .padding(top = 5.dp)
                )
            }
        }

    }

    @Composable
    fun Bar(
        modifier: Modifier = Modifier,
        value: Int,
        maxValue: Int,
        date: String,
    ) {
        val barWidth = 40.dp
        val minHeight = 16.dp
        val barHeight = maxOf(
            minHeight,
            if (maxValue != 0) 160.dp * (value.toFloat() / maxValue.toFloat()) else 0.dp
        )

        Log.d("BarHeight", "Value: $value, MaxValue: $maxValue, BarHeight: $barHeight")

        Column(
            modifier = modifier
                .height(220.dp)
                .width(barWidth)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Box(
                modifier = modifier
                    .height(barHeight)
                    .width(barWidth)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = if (value > 0) listOf(
                                MaterialTheme.colors.primary.copy(
                                    alpha = ContentAlpha.medium
                                ),
                                MaterialTheme.colors.primary.copy(
                                    alpha = ContentAlpha.medium
                                )
                            ) else listOf(Color.LightGray, Color.LightGray),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        ),
                        shape = RoundedCornerShape(50.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$value",
                    style = MaterialTheme.typography.caption.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = date,
                style = MaterialTheme.typography.caption.copy(
                    fontSize = 10.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(barWidth)
                    .padding(top = 5.dp)
            )
        }
    }


    data class BarCharInput(
        val value: Int,
        val description: String,
        val color: Color,
        val date: String
    )

    data class BarCharInputSQ(
        val value: Int,
        val label: String,
        val color: Color,
        val date: String
    )
}