package com.example.recallify.view.ui.feature.guradian_application.guardiandashboard

import android.content.Intent
import android.net.Uri
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
import com.example.recallify.view.ui.feature.application.tbi_applications.dashboard.DashboardActivity
import com.example.recallify.view.ui.feature.guradian_application.mainsettingpages.GuardianMainSettings
import com.example.recallify.view.ui.feature.guradian_application.guardiandailydiary.GuardianDailyDairyActivity
import com.example.recallify.view.ui.feature.guradian_application.guardiansidequest.GuardianSideQuestActivity
import com.example.recallify.view.ui.feature.guradian_application.guardianthinkfast.GuardianThinkFastActivity
import com.example.recallify.view.ui.resource.controller.BottomBarFiller
import com.example.recallify.view.ui.theme.RecallifyTheme
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.maps.model.LatLng

class GuardiansDashboardActivity : AppCompatActivity() {

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

    @Composable
    fun GuardianDashBoardScreen() {

        val coroutineScope = rememberCoroutineScope()
        val tbiUID = remember { mutableStateOf <String?>(null)}
        val firstName = remember { mutableStateOf<String?>(null) }
        val address = remember { mutableStateOf<String?>(null)}
        val latitude = remember { mutableStateOf<Double?>(null)}
        val longitude = remember { mutableStateOf<Double?>(null)}


        Log.d("latitude", "${latitude.value}")
        Log.d("longitude", "${longitude.value}")

        LaunchedEffect(Unit){

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
            ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(30.dp),
                            contentAlignment = Alignment.TopCenter
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
                            }
                        }
                    }
            }
        }
    }
}
