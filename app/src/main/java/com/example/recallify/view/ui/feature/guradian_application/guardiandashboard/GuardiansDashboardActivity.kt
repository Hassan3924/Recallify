package com.example.recallify.view.ui.feature.guradian_application.guardiandashboard

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recallify.R
import com.example.recallify.view.ui.feature.guradian_application.mainsettingpages.GuardianMainSettings
import com.example.recallify.view.ui.feature.guradian_application.guardiandailydiary.GuardianDailyDairyActivity
import com.example.recallify.view.ui.feature.guradian_application.guardiansidequest.GuardianSideQuestActivity
import com.example.recallify.view.ui.feature.guradian_application.guardianthinkfast.GuardianThinkFastActivity
import com.example.recallify.view.ui.resource.controller.BottomBarFiller
import com.example.recallify.view.ui.theme.RecallifyTheme
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Brush
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import java.time.LocalDate

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

//    override fun onBackPressed() {
//
//        val sharedPref = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
//        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)
//
//        if (isLoggedIn) {
//            // User is logged in, don't allow them to go back to the login screen
//            super.onBackPressedDispatcher
//
//        } else {
//            // User is not logged in, allow them to go back to the login screen
//            Toast.makeText(this, "Please log in to continue", Toast.LENGTH_SHORT).show()
//        }
//    }

    private fun refreshLocationData(tbiUID: String, latitude: MutableState<Double?>, longitude: MutableState<Double?>, address: MutableState<String?>) {
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

        } )
    }
    suspend fun getTBIUserID() : String? = suspendCoroutine {
        cont->
        val auth: FirebaseAuth = Firebase.auth
        val currentUser = auth.currentUser?.uid!!
        val uidRef =  Firebase.database.reference.child("users").child("GuardiansLinkTable").child(currentUser).child("TBI_ID")
        uidRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var tbiUID = snapshot.getValue(String::class.java).toString()
                cont.resume(tbiUID)
            }

            override fun onCancelled(error: DatabaseError) {
                cont.resume(null)
            }

        })
    }

    fun getGoogleMaps(latitude: Double, longitude: Double) : Uri {
        val uriString = "geo:$latitude,$longitude?q=$latitude,$longitude"
        return Uri.parse(uriString)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun GuardianDashBoardScreen() {

        val coroutineScope = rememberCoroutineScope()
        val tbiUID = remember { mutableStateOf <String?>(null)}
        val firstName = remember { mutableStateOf<String?>(null) }
        val address = remember { mutableStateOf<String?>(null)}
        val latitude = remember { mutableStateOf<Double?>(null)}
        val longitude = remember { mutableStateOf<Double?>(null)}
        val scrollState = rememberScrollState()
        val isLoading = remember { mutableStateOf(true) }
        val chartData = remember { mutableStateOf(emptyList<BarCharInput>()) }
        val chartDataSQ = remember { mutableStateOf(emptyList<BarCharInputSQ>()) }
        var selectedBar by remember { mutableStateOf(-1) }


        Log.d("latitude", "${latitude.value}")
        Log.d("longitude", "${longitude.value}")

        LaunchedEffect(Unit){

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
                    val tbiRef = Firebase.database.reference.child("users").child(tbiUID.value!!).child("profile").child("firstname")
                    tbiRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            firstName.value = snapshot.getValue(String::class.java)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
                }
            }

            coroutineScope.launch {
                tbiUID.value = getTBIUserID()
                if (tbiUID.value != null) {
                    val tbiRef = Firebase.database.reference.child("users").child(tbiUID.value!!).child("liveLocation").child("long")
                    tbiRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            longitude.value = snapshot.getValue(Double::class.java)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
                }
            }

            coroutineScope.launch {
                tbiUID.value = getTBIUserID()
                if (tbiUID.value != null) {
                    val tbiRef = Firebase.database.reference.child("users").child(tbiUID.value!!).child("liveLocation").child("lat")
                    tbiRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            latitude.value = snapshot.getValue(Double::class.java)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
                }
            }

            coroutineScope.launch {
                tbiUID.value = getTBIUserID()
                if (tbiUID.value != null) {
                    val tbiRef = Firebase.database.reference.child("users").child(tbiUID.value!!).child("liveLocation").child("address")
                    tbiRef.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            address.value = snapshot.getValue(String::class.java)
                        }

                        override fun onCancelled(error: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })
                }
            }

            coroutineScope.launch {
                tbiUID.value?.let {refreshLocationData(it, latitude, longitude, address)}
            }

        }

        Scaffold(
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
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(30.dp)
                    ) {
                        Column(
                            modifier = Modifier,
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                                Text(
                                    text = "Live Tracking!",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    fontSize = 30.sp,
                                    textAlign = TextAlign.Center)
                                Text(
                                    text = "${firstName.value} is currently at: ${address.value}",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center)
                                Log.d("Address", "${address.value}")

                                val context = LocalContext.current
                                val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
                                        result->
                                }
                                Row(modifier = Modifier.padding(horizontal = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween) {

                                    Button(onClick = {

                                        if (latitude.value != null && longitude.value != null) {
                                            Log.d("InsideButtonlatitude", "${latitude.value}")
                                            Log.d("InsideButtonlongitude", "${longitude.value}")
                                            val uri = getGoogleMaps(latitude = latitude.value!!, longitude = longitude.value!!)
                                            val intent = Intent(Intent.ACTION_VIEW, uri)
                                            intent.setPackage("com.google.android.apps.maps")

                                            if(intent.resolveActivity(context.packageManager) != null) {
                                                launcher.launch(intent)
                                            } else {

                                            }
                                        }


                                    }) {
                                        Text(text = "Open Maps")

                                    }
                                    
                                    Button(onClick = {
                                        tbiUID.value?.let { refreshLocationData(it, latitude, longitude, address) }
                                    }) {
                                        Text(text = "Refresh Location")
                                    }
                                }

                                // Side Quest and Think Fast come here

                                Text(
                                    "Think Fast Progress",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    fontSize = 30.sp,
                                    textAlign = TextAlign.Center
                                )

                                Text(
                                    text = "Score",
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.Black,
                                    fontSize = 20.sp,
                                    textAlign = TextAlign.Center
                                )


                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth(),
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
                                modifier = Modifier,
                                verticalArrangement = Arrangement.spacedBy(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "Side Quest Progress",
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    fontSize = 30.sp,
                                    textAlign = TextAlign.Center
                                )

                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth(),
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

    fun launchInCoroutine(block: suspend () -> Unit) {
        GlobalScope.launch(Dispatchers.Main) {
            block()
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun FirebaseChartData(onDataFetched: (List<BarCharInput>) -> Unit) {

        launchInCoroutine {

            val tbiUID = getTBIUserID()

            val database = Firebase.database.reference.child("analyzeProgressTable").child(tbiUID.toString())

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
                    TODO("Not yet implemented")
                }
            })
        }
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
        maxValue: Int,
        description: String,
        date: String,
        showDescription: Boolean
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun FirebaseChartDataSQ(onDataFetched: (List<BarCharInputSQ>) -> Unit) {

        launchInCoroutine {
            val tbiUID = getTBIUserID()

            val database =
                Firebase.database.reference.child("users").child(tbiUID.toString()).child("viewScoresTableSideQuest")

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

        Column(modifier = modifier
            .height(300.dp)
            .width(barWidth)
            .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom){
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
                Text(text = "$value",
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
                Text(text = label,
                    style = MaterialTheme.typography.caption,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .width(barWidth)
                        .padding(top = 5.dp))
            }
        }

    }


    data class BarCharInput(
        val value: Int,
        val description: String,
        val color: Color,
        val date: String
    )

    data class BarCharInputSQ (
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
