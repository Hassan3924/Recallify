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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import com.example.recallify.R
import com.example.recallify.databinding.ActivityDashboardBinding
import com.example.recallify.view.common.components.DashBoardTopAppBar
import com.example.recallify.view.ui.feature.application.dashboard.NotificationService
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.DailyDiaryActivity
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.Information
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_log.screens.getCurrentDate
import com.example.recallify.view.ui.feature.application.tbi_applications.sidequest.SideQuestActivity
import com.example.recallify.view.ui.feature.application.tbi_applications.tbimainsettings.MainSettingsTBI
import com.example.recallify.view.ui.feature.application.tbi_applications.thinkfast.ThinkFastActivity
import com.example.recallify.view.ui.resource.controller.BottomBarFiller
import com.example.recallify.view.ui.theme.CommonColor
import com.example.recallify.view.ui.theme.RecallifyTheme
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
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

open class DashboardActivity : AppCompatActivity() {
    /*
    * Unused Variables
    * */
    private lateinit var mainbinding: ActivityDashboardBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var currentLocation: MutableLiveData<LatLng>
    private var locationText = ""
    private val label = "User Location"

    /*
    * Firebase related variables
    * */
    val database = FirebaseDatabase.getInstance()
    val locationAdd = database.reference
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    private val userID = auth.currentUser?.uid!!

    /*
    * Date and time formatting variables
    * */
    @RequiresApi(Build.VERSION_CODES.O)
    private val current = LocalDateTime.now()

    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    @RequiresApi(Build.VERSION_CODES.O)
    private val formatted = current.format(formatter)

    @RequiresApi(Build.VERSION_CODES.O)
    val currentDate: String = formatted.toString()

    /*    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {

            super.onLocationResult(locationResult)
            val location = locationResult.lastLocation
            if (location != null) {
                currentLocation.value = LatLng(location.latitude, location.longitude)
            }
            locationResult ?: return
            for (location in locationResult.locations) {
                val lat = location.latitude
                val lng = location.longitude
                locationText= "Current location: $lat, $lng"
                var address = getAddressName(location.latitude,location.longitude)
                Log.d("CurrentLocation : ",locationText)
                addLiveLocation(lat,lng,address)

            }
        }
    }

    fun addLiveLocation(lat:Double,lng:Double,address:String){
        user?.let {
            val userUID = it.uid
            locationAdd.child("users").child(userUID).child("liveLocation").child("lat").setValue(lat)
            locationAdd.child("users").child(userUID).child("liveLocation").child("long").setValue(lng)
            locationAdd.child("users").child(userUID).child("liveLocation").child("address").setValue(address)
        }
    }*/

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        /*        // Request location permissions
        requestLocationPermissions()

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createLocationRequest()

        // Initialize currentLocation variable
        currentLocation = MutableLiveData()
        getCurrentLocation()

        // Start receiving location updates
        startLocationUpdates()*/

        startService(Intent(this, NotificationService::class.java)) //this is for notification

        /**
         * The bottom bar navigation controller
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

        val auth: FirebaseAuth = Firebase.auth

        val scrollState = rememberScrollState()

        val isLoading = remember { mutableStateOf(true) }

        var showDescription by remember {
            mutableStateOf(false)
        }

        val selectedBar by remember {
            mutableStateOf(-1)
        }

        val chartData = remember { mutableStateOf(emptyList<BarCharInput>()) }

        val chartDataSQ = remember { mutableStateOf(emptyList<BarCharInputSQ>()) }

        var isActivityLoading by remember { mutableStateOf(true) }

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

            FirebaseChartData { fetchedData ->
                chartData.value = fetchedData
                isLoading.value = false
            }

            FirebaseChartDataSQ { fetchedData ->
                chartDataSQ.value = fetchedData
                isLoading.value = false
            }
        }

        LaunchedEffect(getCurrentDate()) {
            val latestDB = FirebaseDatabase.getInstance().reference
            val latestActivityPath = latestDB.child("users")
                .child(userID)
                .child("dailyDairyDummy")
                .child(getCurrentDate())

            isActivityLoading = true

            latestActivityPath.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (childSnapshot in snapshot.children) {
//                            val key = childSnapshot.key!!
//                            Log.d("key_checker", "nrgwKey: $key")
                            date.value = childSnapshot.child("date").value.toString()
                            time.value = childSnapshot.child("time").value.toString()
                            locationName.value =
                                childSnapshot.child("locationName").value.toString()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    isActivityLoading = false
                }
            })
        }

        LaunchedEffect(Unit) {
            val latestDB = FirebaseDatabase.getInstance().reference
            val liveTrackLocation = latestDB.child("users")
                .child(userID)
                .child("liveLocation")

            liveTrackLocation.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        locationAddress.value = snapshot.child("address").value.toString()
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
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp),
//                            verticalArrangement = Arrangement.spacedBy(20.dp),
//                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Think fast progress",
                                style = MaterialTheme.typography.body1.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
//                            Text(
//                                text = "Score",
//                                fontWeight = FontWeight.SemiBold,
//                                color = Color.Black,
//                                fontSize = 20.sp,
//                                textAlign = TextAlign.Center
//                            )

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
                                        // First value as date, second value as score of that date
                                        chartData.value,
                                        modifier = Modifier.fillMaxWidth(),
                                        selectedBar = selectedBar,
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier.padding(vertical = 8.dp),
//                                verticalArrangement = Arrangement.spacedBy(20.dp),
//                                horizontalAlignment = Alignment.CenterHorizontally
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
                                            // First value as date, second value as score of that date
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun FirebaseChartData(onDataFetched: (List<BarCharInput>) -> Unit) {

        Log.d("FirebaseChartDataAtTheStart", "FirebaseChartData called")

        val auth: FirebaseAuth = Firebase.auth

        val database =
            Firebase.database.reference.child("analyzeProgressTable").child(auth.currentUser?.uid!!)
        val colors = listOf(
            Color.White,
            Color.Gray,
            Color.Yellow,
            Color.Cyan,
            Color.Magenta,
            Color.Blue
        )

        val sevenDays = (0..5).map { LocalDate.now().minusDays(it.toLong()) }

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("FirebaseChartData", "onDataChange called")

                val rawData = snapshot.children.toList()
                Log.d("FirebaseChartData", "User data: ${snapshot.getValue()}")


                val barCharInputData = sevenDays.map { date ->
                    val dataSnapshot = snapshot.child(date.toString())
                    val totalCorrect =
                        dataSnapshot.child("totalCorrect").getValue(Int::class.java) ?: 0
                    val totalPlay = dataSnapshot.child("totalPlay").getValue(Int::class.java) ?: 0

                    val score = totalCorrect

                    Log.d(
                        "FirebaseChartData2",
                        "Fetched Date: $date, totalCorrect: $totalCorrect, totalPlay: $totalPlay"
                    )

                    BarCharInput(
                        score,
                        date.toString(),
                        colors[sevenDays.indexOf(date) % colors.size],
                        date.toString()
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun FirebaseChartDataSQ(onDataFetched: (List<BarCharInputSQ>) -> Unit) {

        Log.d("FirebaseChartDataAtTheStart", "FirebaseChartData called")

        val auth: FirebaseAuth = Firebase.auth

        val database =
            Firebase.database.reference.child("users").child(auth.currentUser?.uid!!)
                .child("viewScoresTableSideQuest")
        val colors = listOf(
            Color.White,
            Color.Gray,
            Color.Yellow,
            Color.Cyan,
            Color.Magenta,
            Color.Blue
        )

        val sevenDays = (0..5).map { LocalDate.now().minusDays(it.toLong()) }

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("FirebaseChartData", "onDataChange called")

                val rawData = snapshot.children.toList()
                Log.d("FirebaseChartData", "User data: ${snapshot.getValue()}")


                val barCharInputData = sevenDays.map { date ->
                    val dataSnapshot = snapshot.child(date.toString())
                    val games = dataSnapshot.children.toList()

                    val score =
                        games.firstOrNull()?.child("correct")?.getValue(Int::class.java) ?: 0

                    Log.d(
                        "FirebaseChartDataSQ",
                        "Fetched Date: $date, totalCorrect: $score"
                    )

                    Log.d(
                        "FirebaseChartDataSQ Snapshot",
                        "Fetched Date: $dataSnapshot"
                    )

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
        selectedBar: Int,
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
                inputList.forEachIndexed { index, input ->
                    Bar(
                        modifier = Modifier,
                        primaryColor = input.color,
                        value = input.value,
                        maxValue = maxValue,
//                        description = input.description,
                        date = input.date,
//                        showDescription = selectedBar == index
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
                        primaryColor = input.color,
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
        primaryColor: Color,
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
                            colors = listOf(Color.Gray, primaryColor),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
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
                style = MaterialTheme.typography.caption,
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
        primaryColor: Color,
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
                            colors = listOf(Color.Gray, primaryColor),
                            startY = 0f,
                            endY = Float.POSITIVE_INFINITY
                        )
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
                style = MaterialTheme.typography.caption,
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

    data class ScoreData(
        @JvmField
        @PropertyName("date")
        val date: String = "",

        @JvmField
        @PropertyName("score")
        val score: Int = 0,
    )

    @Composable
    fun DescriptionUI() {
        Column(
            modifier = Modifier.padding(horizontal = 30.dp)
        ) {
            Text(
                text = "Description",
                fontSize = 16.sp,
                color = Color.DarkGray,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Make a youtube video on  Task management app user " +
                        "interface and make sure to post it on youtube, " +
                        "also create thumbnail and other pages",
                fontSize = 11.sp,
                color = CommonColor,
                fontWeight = FontWeight.Normal
            )
        }
    }


    @Composable
    fun LineChart(dataPoints: List<Float>, modifier: Modifier = Modifier) {

        val strokeWidth = 4.dp
        val stroke = Stroke(width = strokeWidth.value, cap = StrokeCap.Round)

        Canvas(
            modifier = modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            val width = size.width
            val height = size.height
            val maxValue = dataPoints.maxOrNull() ?: 0f
            val minValue = dataPoints.minOrNull() ?: 0f
            val yScale = (maxValue - minValue) / height
            val xScale = width / (dataPoints.size - 1)

            for (i in 0 until dataPoints.size - 1) {
                val startX = i * xScale
                val startY = height - (dataPoints[i] - minValue) / yScale
                val endX = (i + 1) * xScale
                val endY = height - (dataPoints[i + 1] - minValue) / yScale

                drawLine(
                    color = Color.Blue,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = strokeWidth.value,
                    cap = StrokeCap.Round
                )

            }

        }

    }

    private fun listenForDataUpdates(
        database: DatabaseReference,
        onDataUpdate: (List<Float>) -> Unit
    ) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val dataPoints: List<Float> = parseDataSnapshot(dataSnapshot)
                onDataUpdate(dataPoints)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    private fun parseDataSnapshot(dataSnapshot: DataSnapshot): List<Float> {
        val dataPoints = mutableListOf<Float>()

        dataSnapshot.children.forEach { childSnapshot ->
            val dataPoint = childSnapshot.getValue(Float::class.java)
            dataPoint?.let { dataPoints.add(it) }
        }

        return dataPoints
    }


/*
    @Composable
    fun LineChart(modifier: Modifier = Modifier, lineData: LineData) {
        Box(modifier) {
            AndroidView(factory = { context ->
                LineChart(context).apply {
                    data = lineData
                }
            })
        }
    }

    @Composable
    fun StatisticIndicatorUI() {
        Column(
            modifier = Modifier
                .height(120.dp) ,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            IndicatorItemUI(text = "Finish on time")
            IndicatorItemUI(color = light_Secondary, text = "Past the deadline")
            IndicatorItemUI(color = Color(0xFFE3E5E7), text = "Still ongoing")
        }
    }

    @Composable
    fun IndicatorItemUI(color: Color = light_Primary,text:String) {
        Row {
            Icon(
                painter = painterResource(id = coil.base.R.drawable.button_shape),
                contentDescription = "",
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,

                fontSize = 12.sp,
                color = Color(0xFF818181),
                fontWeight = FontWeight.Normal
            )
        }
    }


    //Live location Tracking functions #Ridinbal
    private fun requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest.create().apply {
            interval = LOCATION_UPDATE_INTERVAL
            fastestInterval = FASTEST_LOCATION_UPDATE_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                location?.let {
                    currentLocation.value = LatLng(location.latitude, location.longitude)
                }
            }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 100
        private const val LOCATION_UPDATE_INTERVAL: Long = 5000
        private const val FASTEST_LOCATION_UPDATE_INTERVAL: Long = 2000
    }
    private fun getAddressName(lat:Double, lon:Double): String{

        var addressName = ""
        var geoCoder = Geocoder(this, Locale.getDefault())
        var address = geoCoder.getFromLocation(lat,lon,1)

        if (address != null) {
            addressName = address[0].getAddressLine(0)

        }
        return addressName*/

}