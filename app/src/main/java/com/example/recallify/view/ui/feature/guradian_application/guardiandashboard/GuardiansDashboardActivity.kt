package com.example.recallify.view.ui.feature.guradian_application.guardiandashboard

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recallify.R
import com.example.recallify.view.common.components.RecallifyCustomHeader2
import com.example.recallify.view.common.components.DashBoardTopAppBar
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_log.screens.getCurrentDate
import com.example.recallify.view.ui.feature.guradian_application.guardiandailydiary.GuardianDailyDairyActivity
import com.example.recallify.view.ui.feature.guradian_application.guardiansidequest.GuardianSideQuestActivity
import com.example.recallify.view.ui.feature.guradian_application.guardianthinkfast.GuardianThinkFastActivity
import com.example.recallify.view.ui.feature.guradian_application.mainsettingpages.GuardianMainSettings
import com.example.recallify.view.ui.resource.controller.BottomBarFiller
import com.example.recallify.view.ui.theme.RecallifyTheme
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class GuardiansDashboardActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_dashboard)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        bottomNavigationView.selectedItemId = R.id.bottom_home

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.bottom_home -> true
                R.id.bottom_daily_diary -> {
                    startActivity(
                        Intent(
                            applicationContext,
                            GuardianDailyDairyActivity::class.java
                        )
                    )
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

        val dashBoardCompose: ComposeView = findViewById(R.id.activity_dash_board_screen)
        dashBoardCompose.setContent {
            RecallifyTheme {
                GuardianDashBoardScreen()
            }
        }
    }

    private fun refreshLocationData(
        tbiUID: String,
        latitude: MutableState<Double?>,
        longitude: MutableState<Double?>,
        address: MutableState<String?>
    ) {
        val tbiRef = Firebase.database.reference.child("users").child(tbiUID).child("liveLocation")

        tbiRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                latitude.value = snapshot.child("lat").getValue(Double::class.java)
                longitude.value = snapshot.child("long").getValue(Double::class.java)
                address.value = snapshot.child("address").getValue(String::class.java)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

    private suspend fun getTBIUserID(): String? = suspendCoroutine { cont ->
        val auth: FirebaseAuth = Firebase.auth
        val currentUser = auth.currentUser?.uid!!
        val uidRef = Firebase.database.reference.child("users").child("GuardiansLinkTable")
            .child(currentUser).child("TBI_ID")
        uidRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tbiUID = snapshot.getValue(String::class.java).toString()
                cont.resume(tbiUID)
            }

            override fun onCancelled(error: DatabaseError) {
                cont.resume(null)
            }

        })
    }

    private fun getGoogleMaps(latitude: Double, longitude: Double): Uri {
        val uriString = "geo:$latitude,$longitude?q=$latitude,$longitude"
        return Uri.parse(uriString)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun GuardianDashBoardScreen() {
        val coroutineScope = rememberCoroutineScope()
        val tbiUID = remember { mutableStateOf<String?>(null) }
        val firstName = remember { mutableStateOf<String?>(null) }
        val address = remember { mutableStateOf<String?>(null) }
        val latitude = remember { mutableStateOf<Double?>(null) }
        val longitude = remember { mutableStateOf<Double?>(null) }
        val scrollState = rememberScrollState()
        val isLoading = remember { mutableStateOf(true) }
        val chartData = remember { mutableStateOf(emptyList<BarCharInput>()) }
        val chartDataSQ = remember { mutableStateOf(emptyList<BarCharInputSQ>()) }
        val selectedBar by remember { mutableStateOf(-1) }

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
        val context = LocalContext.current
        val launcher =
            rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {}

        LaunchedEffect(Unit) {

            FirebaseChartData { fetchedData ->
                chartData.value = fetchedData
                isLoading.value = false
            }

            FirebaseChartDataSQ { fetchedData ->
                chartDataSQ.value = fetchedData
                isLoading.value = false
            }

            coroutineScope.launch {
                tbiUID.value = getTBIUserID()
                if (tbiUID.value != null) {
                    val tbiRef = Firebase.database.reference.child("users").child(tbiUID.value!!)
                        .child("profile").child("firstname")
                    tbiRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            firstName.value = snapshot.getValue(String::class.java)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d("onCancelledFirebase", "Error Message: ${error.toException()}")
                        }

                    })
                }
            }

            coroutineScope.launch {
                tbiUID.value = getTBIUserID()
                if (tbiUID.value != null) {
                    val tbiRef = Firebase.database.reference.child("users").child(tbiUID.value!!)
                        .child("liveLocation").child("long")
                    tbiRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            longitude.value = snapshot.getValue(Double::class.java)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d("onCancelledFirebase", "Error Message: ${error.toException()}")
                        }

                    })
                }
            }

            coroutineScope.launch {
                tbiUID.value = getTBIUserID()
                if (tbiUID.value != null) {
                    val tbiRef = Firebase.database.reference.child("users").child(tbiUID.value!!)
                        .child("liveLocation").child("lat")
                    tbiRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            latitude.value = snapshot.getValue(Double::class.java)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d("onCancelledFirebase", "Error Message: ${error.toException()}")
                        }

                    })
                }
            }

            coroutineScope.launch {
                tbiUID.value = getTBIUserID()
                if (tbiUID.value != null) {
                    val tbiRef = Firebase.database.reference.child("users").child(tbiUID.value!!)
                        .child("liveLocation").child("address")
                    tbiRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            address.value = snapshot.getValue(String::class.java)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            Log.d("onCancelledFirebase", "Error Message: ${error.toException()}")
                        }

                    })
                }
            }

            coroutineScope.launch {
                tbiUID.value?.let { refreshLocationData(it, latitude, longitude, address) }
            }

        }

        LaunchedEffect(getCurrentDate()) {
            tbiUID.value = getTBIUserID()
            if (tbiUID.value != null) {
                val latestDB = FirebaseDatabase.getInstance().reference
                val latestActivityPath = latestDB.child("users")
                    .child(tbiUID.value.toString())
                    .child("dailyDairyDummy")
                    .child(getCurrentDate())

                isActivityLoading = true

                latestActivityPath.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            for (childSnapshot in snapshot.children) {
                                val key = childSnapshot.key!!
                                Log.d("key_checker", "nrgwKey: $key")
                                date.value =
                                    childSnapshot.child("date").value.toString()
                                time.value =
                                    childSnapshot.child("time").value.toString()
                                locationName.value =
                                    childSnapshot.child("locationName").value.toString()
                                locationAddress.value =
                                    childSnapshot.child("locationAddress").value.toString()
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        isActivityLoading = false
                    }
                })
            }

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
                        .fillMaxWidth()
                        .padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        )
                        .verticalScroll(scrollState)
                ) {
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painterResource(id = R.drawable.round_location_on_24),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        RecallifyCustomHeader2(title = "Live tracking")
                    }
                    Spacer(modifier = Modifier.size(10.dp))
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
                                        horizontal = 10.dp,
                                        vertical = 6.dp
                                    )
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.Top,
                                    horizontalAlignment = Alignment.Start
                                ) {
                                    Text(text = "${
                                        firstName.value?.replaceFirstChar {
                                            if (it.isLowerCase()) it.titlecase(
                                                Locale.ROOT
                                            ) else it.toString()
                                        }
                                    } is currently at",
                                        style = MaterialTheme.typography.body2.copy(
                                            color = MaterialTheme.colors.onBackground
                                        ))
                                    Text(
                                        text = "${address.value}.",
                                        style = MaterialTheme.typography.body2.copy(
                                            color = MaterialTheme.colors.primary.copy(
                                                alpha = 1f
                                            )
                                        )
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = {
                                            if (latitude.value != null && longitude.value != null) {
                                                val uri = getGoogleMaps(
                                                    latitude = latitude.value!!,
                                                    longitude = longitude.value!!
                                                )
                                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                                intent.setPackage("com.google.android.apps.maps")

                                                if (intent.resolveActivity(context.packageManager) != null) {
                                                    launcher.launch(intent)
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            backgroundColor = MaterialTheme.colors.primaryVariant
                                        )
                                    ) {
                                        Text(
                                            text = "See location",
                                            style = MaterialTheme.typography.button
                                        )
                                    }
                                    Spacer(modifier = Modifier.padding(horizontal = 6.dp))
                                    Button(
                                        onClick = {
                                            tbiUID.value?.let {
                                                refreshLocationData(
                                                    it,
                                                    latitude,
                                                    longitude,
                                                    address
                                                )
                                            }
                                        }
                                    ) {
                                        Text(
                                            text = "Refresh",
                                            style = MaterialTheme.typography.button
                                        )
                                    }
                                }
                                if (locationName.value.isNotBlank()) {
                                    Text(
                                        text = "last seen at -",
                                        style = MaterialTheme.typography.body2.copy(
                                            color = MaterialTheme.colors.onBackground
                                        )
                                    )
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
                    ) {
                        Text(
                            text = "Think fast progress",
                            style = MaterialTheme.typography.body1.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            modifier = Modifier.padding(top = 16.dp)
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
                                BarChart(
                                    chartData.value,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
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
                            modifier = Modifier.padding(top = 16.dp)
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

    fun launchInCoroutine(block: suspend () -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            block()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun FirebaseChartData(onDataFetched: (List<BarCharInput>) -> Unit) {

        launchInCoroutine {

            val tbiUID = getTBIUserID()

            val database =
                Firebase.database.reference.child("analyzeProgressTable").child(tbiUID.toString())

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
                        val totalPlay =
                            dataSnapshot.child("totalPlay").getValue(Int::class.java) ?: 0

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
                    TODO("Not yet implemented")
                }
            })
        }
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


                inputList.forEachIndexed { index, input ->

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
                style = MaterialTheme.typography.caption,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .width(barWidth)
                    .padding(top = 5.dp)
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun FirebaseChartDataSQ(onDataFetched: (List<BarCharInputSQ>) -> Unit) {

        launchInCoroutine {
            val tbiUID = getTBIUserID()

            val database =
                Firebase.database.reference.child("users").child(tbiUID.toString())
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
                    Log.e("FirebaseChartDataonCancelled", "onCalled called ${error.message}")
                }

            })
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
}
