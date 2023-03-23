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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.recallify.R
import com.example.recallify.view.ui.feature.application.dashboard.NotificationService
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.DailyDiaryActivity
import com.example.recallify.view.ui.feature.application.tbi_applications.sidequest.SideQuestActivity
import com.example.recallify.view.ui.feature.application.tbi_applications.tbimainsettings.MainSettingsTBI
import com.example.recallify.view.ui.feature.application.tbi_applications.thinkfast.ThinkFastActivity
import com.example.recallify.view.ui.resource.controller.BottomBarFiller
import com.example.recallify.view.ui.theme.CommonColor
import com.example.recallify.view.ui.theme.RecallifyTheme
import com.example.recallify.view.ui.theme.light_Primary
import com.example.recallify.view.ui.theme.light_Secondary
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.TopCenter
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.example.recallify.view.common.components.TabPage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.PropertyName
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import okio.ProtocolException
import java.time.LocalDate


import android.Manifest

import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.net.Uri

import android.os.Looper


import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.example.recallify.databinding.ActivityDashboardBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.location.FusedLocationProviderClient

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class DashboardActivity : AppCompatActivity() {
    lateinit var mainbinding:ActivityDashboardBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var currentLocation: MutableLiveData<LatLng>
    var locationText=""
    private val label = "User Location"
    val database = FirebaseDatabase.getInstance()
    val locationAdd = database.reference
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    @RequiresApi(Build.VERSION_CODES.O)
    val current = LocalDateTime.now()
    @RequiresApi(Build.VERSION_CODES.O)
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    @RequiresApi(Build.VERSION_CODES.O)
    val formatted = current.format(formatter)
    @RequiresApi(Build.VERSION_CODES.O)
    val currentDate:String = formatted.toString()

    private val locationCallback = object : LocationCallback() {
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
                Log.d("Currentlocation : ",locationText)
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
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Request location permissions
        requestLocationPermissions()

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createLocationRequest()

        // Initialize currentLocation variable
        currentLocation = MutableLiveData()
        getCurrentLocation()

        // Start receiving location updates
        startLocationUpdates()

        startService(Intent(this, NotificationService::class.java))
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

        val isLoading = remember { mutableStateOf(true) }

        var showDescription by remember {
            mutableStateOf(false)
        }

        var selectedBar by remember {
            mutableStateOf(-1)
        }

        val auth: FirebaseAuth = Firebase.auth

        val chartData = remember { mutableStateOf(emptyList<BarCharInput>()) }

        LaunchedEffect(Unit) {
            FirebaseChartData { fetchedData ->
                chartData.value = fetchedData
                isLoading.value = false
            }
        }


        Scaffold (
            bottomBar = { BottomBarFiller() },
            backgroundColor = MaterialTheme.colors.surface
        ) { paddingValue ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValue)
//                    .verticalScroll(scrollState)
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .padding(horizontal = 16.dp)
                        .padding(4.dp)
                        .fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(30.dp),
                        contentAlignment = TopCenter
                    ) {
                        Column(
                            modifier = Modifier,
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Think Fast Progress",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                fontSize = 30.sp,
                                textAlign = TextAlign.Center
                            )

                            Text(text = "Score",
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Black,
                                fontSize = 20.sp,
                                textAlign = TextAlign.Center)


                            Column(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if(isLoading.value) {
                                    CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
                                }
                                else {

                                        BarChart(
                                            // First value as date, second value as score of that date
                                            chartData.value,
                                            modifier = Modifier.fillMaxWidth(),
                                            selectedBar = selectedBar,
                                        )

                                }
                            }
                        }
//
//                        Column(
//                            modifier = Modifier,
//                            verticalArrangement = Arrangement.spacedBy(20.dp),
//                            horizontalAlignment = Alignment.CenterHorizontally
//                        ) {
//                            Text(
//                                "Side Quest Progress",
//                                fontWeight = FontWeight.Bold,
//                                color = Color.Black,
//                                fontSize = 30.sp,
//                                textAlign = TextAlign.Center
//                            )
//                        }
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun FirebaseChartData(onDataFetched: (List<BarCharInput>) -> Unit) {
        Log.d("FirebaseChartDataAtTheStart", "FirebaseChartData called")

        val auth: FirebaseAuth = Firebase.auth

        val database =
            Firebase.database.reference.child("analyzeProgressTable").child(auth.currentUser?.uid!!)
        val colors = listOf<Color>(
            Color.White,
            Color.Gray,
            Color.Yellow,
            Color.Cyan,
            Color.Magenta,
            Color.Blue
        )

        val sevenDays = (0..5).map { LocalDate.now().minusDays(it.toLong())}

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
                Log.e("FirebaseChartDataonCancelled", "onCalled called ${error.message}")
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
                            description = input.description,
                            date = input.date,
                            showDescription = selectedBar == index
                        )
                }
            }
        }
    }

    @Composable
    fun Bar(
        modifier: Modifier = Modifier,
        primaryColor: Color,
        value: Int,
        maxValue : Int,
        description: String,
        date: String,
        showDescription: Boolean
    ) {

        val barWidth = 40.dp
        val minHeight = 16.dp
        val barHeight = maxOf(minHeight, if (maxValue != 0) 160.dp * (value.toFloat() / maxValue.toFloat()) else 0.dp)


        Log.d("BarHeight", "Value: $value, MaxValue: $maxValue, BarHeight: $barHeight")

        Column(
            modifier = modifier
                .height(300.dp)
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
//            if (showDescription) {
//                Text(
//                    text = date,
//                    style = MaterialTheme.typography.caption,
//                    textAlign = TextAlign.Center,
//                    modifier = Modifier.width(barWidth)
//                )
//            }
            Text(text = date,
            style = MaterialTheme.typography.caption,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .width(barWidth)
                .padding(top = 5.dp)
            )
        }
    }



    data class BarCharInput(
        val value : Int,
        val description : String,
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
    fun StatisticUI() {
        Column(
            modifier = Modifier.padding(30.dp)
        ) {
            Text(
                text = "Jetpack Compose UI Design",
                fontSize = 16.sp,
                color = MaterialTheme.colors.primary,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = CenterVertically
            ) {
                Row {
                    Icon(
                        painter = painterResource(id = R.drawable.round_access_time_24),
                        contentDescription = "",
                        tint = Color(0xFF818181),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "09.00 AM - 11.00 AM",
                        fontSize = 12.sp,
                        color = Color(0xFF818181),
                        fontWeight = FontWeight.Medium
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE1E3FA))
                        .border(
                            width = 0.dp,
                            color = Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = "On Going",
                        fontSize = 10.sp,

                        color = Color(0xFF7885B9)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Statistic",
                fontSize = 16.sp,
                color = CommonColor,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                StatisticProgressUI()
                Spacer(modifier = Modifier.width(12.dp))
                // StatisticIndicatorUI()
            }
        }
    }

    @Composable
    fun TaskCardUI() {
        val annotatedString1 = AnnotatedString.Builder("4/6 Task")
            .apply {
                addStyle(
                    SpanStyle(
                        color = MaterialTheme.colors.primaryVariant,
                    ), 0, 3
                )
            }

        Card(
            backgroundColor = Color.White,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp)
                .padding(top = 40.dp),
            elevation = 0.dp,
            shape = MaterialTheme.shapes.large
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Daily Task",
                        fontSize = 12.sp,
                        color = MaterialTheme.colors.primaryVariant,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row {
                        Icon(
                            painter = painterResource(id = R.drawable.round_access_time_24),
                            contentDescription = "",
                            tint = MaterialTheme.colors.primary,
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = annotatedString1.toAnnotatedString(),
                            fontSize = 18.sp,
                            color = MaterialTheme.colors.primary,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }


                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Almost finished,\nkeep it up",
                        fontSize = 13.sp,
                        color = Color(0xFF292D32),
                        fontWeight = FontWeight.Normal
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.large)
                            .border(
                                width = 0.dp,
                                color = Color.Transparent,
                                shape = MaterialTheme.shapes.large
                            ),
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
                        contentPadding = PaddingValues(vertical = 0.dp, horizontal = 16.dp)
                    ) {
                        Text(
                            text = "Daily Task",
                            fontSize = 10.sp,
                            modifier = Modifier.align(alignment = CenterVertically),
                        )
                    }


                }


                ProgressBarUI(percentage = 67f)


            }
        }
    }

    @Composable
    fun ProgressBarUI(percentage: Float) {
        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(100.dp)
                    .padding(6.dp)
            ) {
                drawCircle(
                    SolidColor(Color(0xFFE3E5E7)),
                    size.width / 2,
                    style = Stroke(26f)
                )
                val convertedValue = (percentage / 100) * 360
                drawArc(
                    brush = Brush.linearGradient(
                        colors = listOf(light_Secondary, light_Primary)
                    ),
                    startAngle = -90f,
                    sweepAngle = convertedValue,
                    useCenter = false,
                    style = Stroke(26f, cap = StrokeCap.Round)
                )
            }

            val annotatedString2 = AnnotatedString.Builder("${percentage.toInt()}%\nDone")
                .apply {
                    addStyle(
                        SpanStyle(
                            color = MaterialTheme.colors.secondaryVariant,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Normal
                        ), 4, 8
                    )
                }

            Text(
                text = annotatedString2.toAnnotatedString(),
                fontSize = 14.sp,
                color = MaterialTheme.colors.primaryVariant,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

        }
    }

    @Composable
    fun HeaderUI() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .padding(top = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = CenterVertically
        ) {
            Column {
                Text(
                    text = "Hello, John Recallify",
                    fontSize = 18.sp,
                    color = MaterialTheme.colors.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Let's do your today task",
                    fontSize = 14.sp,
                    color = MaterialTheme.colors.primary,
                    fontWeight = FontWeight.Medium
                )
            }

            Image(
                painter = painterResource(id = R.drawable.image_default),
                contentDescription = "",
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape),
            )

        }
    }

    @Composable
    fun StatisticProgressUI(primaryPercentage: Float = 60f, secondaryPercentage: Float = 15f) {
        Box(
            modifier = Modifier
                .size(120.dp),
            contentAlignment = Center
        ) {
            Canvas(
                modifier = Modifier
                    .size(100.dp)
            ) {
                drawCircle(
                    SolidColor(Color(0xFFE3E5E7)),
                    size.width / 2,
                    style = Stroke(34f)
                )
                val convertedPrimaryValue = (primaryPercentage / 100) * 360
                val convertedSecondaryValue =
                    ((secondaryPercentage / 100) * 360) + convertedPrimaryValue
                drawArc(
                    brush = SolidColor(light_Secondary),
                    startAngle = -90f,
                    sweepAngle = convertedSecondaryValue,
                    useCenter = false,
                    style = Stroke(34f, cap = StrokeCap.Round)
                )
                drawArc(
                    brush = SolidColor(light_Primary),
                    startAngle = -90f,
                    sweepAngle = convertedPrimaryValue,
                    useCenter = false,
                    style = Stroke(34f, cap = StrokeCap.Round)
                )
            }

            val annotatedString2 =
                AnnotatedString.Builder("${(primaryPercentage + secondaryPercentage).toInt()}%\nDone")
                    .apply {
                        addStyle(
                            SpanStyle(
                                color = CommonColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Normal
                            ), 4, 8
                        )
                    }

            Text(
                text = annotatedString2.toAnnotatedString(),

                fontSize = 20.sp,
                color = MaterialTheme.colors.primary,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }

    @Composable
    fun LineChart(dataPoints: List<Float>, modifier: Modifier = Modifier) {

        val strokeWidth = 4.dp
        val stroke = Stroke(width = strokeWidth.value, cap = StrokeCap.Round)

        Canvas (
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

    private fun listenForDataUpdates(database: DatabaseReference, onDataUpdate: (List<Float>) -> Unit) {
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
            dataPoint?.let {dataPoints.add(it)}
        }

        return dataPoints
    }
//
//    @Composable
//    fun LineChart(modifier: Modifier = Modifier, lineData: LineData) {
//        Box(modifier) {
//            AndroidView(factory = { context ->
//                LineChart(context).apply {
//                    data = lineData
//                }
//            })
//        }
//    }

//    @Composable
//    fun StatisticIndicatorUI() {
//        Column(
//            modifier = Modifier
//                .height(120.dp) ,
//            verticalArrangement = Arrangement.SpaceBetween
//        ) {
//            IndicatorItemUI(text = "Finish on time")
//            IndicatorItemUI(color = light_Secondary, text = "Past the deadline")
//            IndicatorItemUI(color = Color(0xFFE3E5E7), text = "Still ongoing")
//        }
//    }

//    @Composable
//    fun IndicatorItemUI(color: Color = light_Primary,text:String) {
//        Row {
//            Icon(
//                painter = painterResource(id = coil.base.R.drawable.button_shape),
//                contentDescription = "",
//                tint = color,
//                modifier = Modifier.size(20.dp)
//            )
//            Spacer(modifier = Modifier.width(12.dp))
//            Text(
//                text = text,
//
//                fontSize = 12.sp,
//                color = Color(0xFF818181),
//                fontWeight = FontWeight.Normal
//            )
//        }
//    }

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
        return addressName
    }
}