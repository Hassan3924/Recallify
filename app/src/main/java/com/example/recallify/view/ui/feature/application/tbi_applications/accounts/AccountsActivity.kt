package com.example.recallify.view.ui.feature.application.tbi_applications.accounts

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.work.*
import androidx.work.WorkRequest
import com.example.recallify.R
import com.example.recallify.view.common.function.ActivityWorker
import com.example.recallify.view.common.resources.AccountsTopAppBar
import com.example.recallify.view.ui.feature.application.tbi_applications.dashboard.DashboardActivity
import com.example.recallify.view.ui.feature.security.signin.LoginActivity
import com.example.recallify.view.ui.resource.controller.BottomBarFiller
import com.example.recallify.view.ui.theme.RecallifyTheme
import com.example.recallify.view.ui.theme.light_Secondary
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.util.*
import java.util.concurrent.TimeUnit
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign


val copiedLocation = mutableStateOf("")
val copiedLongitude = mutableStateOf("")
val copiedLatitude = mutableStateOf("")
val activityWorkTimer = mutableStateOf(20L)

class AccountsActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var currentLocation: MutableLiveData<LatLng>
    private lateinit var activityWorkRequest: WorkRequest

    private val databaseReference = FirebaseDatabase.getInstance().reference
    private val user = FirebaseAuth.getInstance().currentUser
    private var locationText: String = ""

    private val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    private val locationCallback = object : LocationCallback() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun onLocationResult(locationResult: LocationResult) {

            super.onLocationResult(locationResult)
            val location = locationResult.lastLocation
            if (location != null) {
                currentLocation.value = LatLng(location.latitude, location.longitude)
            }
            locationResult ?: return
            for (userLocation in locationResult.locations) {
                val lat = userLocation.latitude
                val lng = userLocation.longitude
                locationText = "Current location: $lat, $lng"
                val address = getAddressName(userLocation.latitude, userLocation.longitude)
                copiedLocation.value = address
                copiedLatitude.value = lat.toString()
                copiedLongitude.value = lng.toString()
                Log.d("CopiedLocation: ", "${copiedLocation.value} ==> $address")
                Log.d("Current-location : ", locationText)
                addLiveLocation(lat, lng, address)
            }
        }
    }

    fun addLiveLocation(lat: Double, lng: Double, address: String) {
        user?.let {
            val userUID = it.uid
            databaseReference.child("users").child(userUID).child("liveLocation").child("lat")
                .setValue(lat)
            databaseReference.child("users").child(userUID).child("liveLocation").child("long")
                .setValue(lng)
            databaseReference.child("users").child(userUID).child("liveLocation").child("address")
                .setValue(address)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_accounts)

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

        // Start Activity Work manager
        activityWorkRequest =
            PeriodicWorkRequestBuilder<ActivityWorker>(20, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()


        val accountsCompose: ComposeView = findViewById(R.id.activity_accounts_screen)
        accountsCompose.setContent {
            RecallifyTheme {
                AccountsScreen()
            }
        }
    }

    @Composable
    fun AccountsScreen() {
        val auth: FirebaseAuth = Firebase.auth
        val database = Firebase.database.reference.child("users")
        val current = auth.currentUser?.uid!!
        val scaffoldState = rememberScaffoldState()

        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                AccountsTopAppBar { LogoutButton(activity = this@AccountsActivity) }
            },
            bottomBar = { BottomBarFiller() },
            backgroundColor = MaterialTheme.colors.surface
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(vertical = 8.dp),
                    Arrangement.Center,
                    Alignment.CenterHorizontally
                ) {

                    var firstName: String by remember { mutableStateOf("") }
                    var lastName: String by remember { mutableStateOf("") }
                    var email: String by remember { mutableStateOf("") }
                    var password: String by remember { mutableStateOf("") }
                    var PIN: String by remember { mutableStateOf("") }

                    LaunchedEffect(Unit) {
                        database.child(current).child("profile").child("firstname")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    firstName = dataSnapshot.getValue(String::class.java) ?: ""
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }
                            })

                        database.child(current).child("profile").child("lastname")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    lastName = dataSnapshot.getValue(String::class.java) ?: ""
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }
                            })

                        database.child(current).child("profile").child("email")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    email = dataSnapshot.getValue(String::class.java) ?: ""
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }
                            })

                        database.child(current).child("profile").child("password")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    password = dataSnapshot.getValue(String::class.java) ?: ""
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }
                            })

                        database.child(current).child("profile").child("pin")
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    PIN = dataSnapshot.getValue(String::class.java) ?: ""
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    TODO("Not yet implemented")
                                }
                            })
                    }

                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = "First Name:")
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = firstName)
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 10.dp)
                    ) {
                        Text(text = "Last Name:")
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = lastName)
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 10.dp)
                    ) {
                        Text(text = "Email:")
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = email)
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 10.dp)
                    ) {
                        Text(text = "PIN:")
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = PIN)
                    }
                    Spacer(modifier = Modifier.padding(vertical = 6.dp))
                    // The expandable preferences + switch to enable the work manager
                    Text(
                        text = "Activity preferences",
                        style = MaterialTheme.typography.caption.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
//                    CustomCard()
                    Spacer(modifier = Modifier.padding(vertical = 10.dp))

                    Button(onClick = {
                        val intent = Intent(this@AccountsActivity, DashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                    }) {
                        Text(text = "Go to Dashboard")
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun CustomCard() {
        var expandedCard by remember {
            mutableStateOf(false)
        }
        var expandedTimer by remember {
            mutableStateOf(false)
        }
        val checkedState = remember {
            mutableStateOf(true)
        }
        val timer = remember { mutableStateOf(15L) }

        var turnOff by remember {
            mutableStateOf(true)
        }

        if (!turnOff) {
            activityWorkTimer.value = timer.value
        } else {
            activityWorkTimer.value = 16L
        }

        if (checkedState.value) {
            WorkManager
                .getInstance(this@AccountsActivity)
                .enqueue(activityWorkRequest)
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .animateContentSize(
                    animationSpec = tween(
                        durationMillis = 300,
                        easing = LinearOutSlowInEasing
                    )
                ),
            shape = RoundedCornerShape(4.dp),
            onClick = {
                expandedCard = !expandedCard
            },
            backgroundColor = if (!expandedCard) MaterialTheme.colors.background else light_Secondary
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Auto activities",
                        fontSize = MaterialTheme.typography.button.fontSize,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${timer.value} mins",
                            fontSize = MaterialTheme.typography.body2.fontSize,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            modifier = Modifier.padding(4.dp)
                        )
                        IconButton(
                            modifier = Modifier.alpha(ContentAlpha.medium),
                            onClick = {
                                expandedCard = !expandedCard

                            }) {
                            if (expandedCard) {
                                Icon(
                                    painter = painterResource(id = R.drawable.round_arrow_drop_down_24),
                                    contentDescription = null
                                )
                            } else {
                                Icon(
                                    painter = painterResource(id = R.drawable.round_arrow_left_24),
                                    contentDescription = null
                                )
                            }
                        }
                    }
                }
                if (expandedCard) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colors.background)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Build an activity by introducing a new location.",
                                style = MaterialTheme.typography.body1.copy(
                                    fontSize = 14.sp
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(2.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                ExposedDropdownMenuBox(
                                    modifier = Modifier
                                        .weight(5f)
                                        .padding(vertical = 8.dp),
                                    expanded = expandedTimer,
                                    onExpandedChange = { expandedTimer = it }
                                ) {
                                    OutlinedTextField(
                                        modifier = Modifier.fillMaxWidth(),
                                        value = timer.value.toString(),
                                        onValueChange = { },
                                        readOnly = true,
                                        enabled = turnOff,
                                        label = { Text("Time interval") },
                                        placeholder = { Text("Select time interval (minutes)") },
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(
                                                expanded = expandedTimer
                                            )
                                        },
                                        colors = ExposedDropdownMenuDefaults.textFieldColors(
                                            backgroundColor = Color.Transparent,
                                            focusedIndicatorColor = MaterialTheme.colors.primary,
                                            focusedTrailingIconColor = MaterialTheme.colors.primary,
                                        ),
                                    )
                                    ExposedDropdownMenu(
                                        expanded = expandedTimer,
                                        modifier = Modifier.width(50.dp),
                                        onDismissRequest = { expandedTimer = false }
                                    ) {
                                        DropdownMenuItem(
                                            content = {
                                                Text(text = "15 mins")
                                            },
                                            onClick = {
                                                timer.value = 15
                                                expandedTimer = false
                                            },
                                            enabled = turnOff
                                        )
                                        DropdownMenuItem(
                                            content = {
                                                Text(text = "25 mins")
                                            },
                                            onClick = {
                                                timer.value = 25
                                                expandedTimer = false
                                            },
                                            enabled = turnOff
                                        )
                                        DropdownMenuItem(
                                            content = {
                                                Text(text = "35 mins")
                                            },
                                            onClick = {
                                                timer.value = 35
                                                expandedTimer = false
                                            },
                                            enabled = turnOff
                                        )
                                    }
                                }
                                Switch(
                                    checked = checkedState.value,
                                    onCheckedChange = {
                                        checkedState.value = it
                                        turnOff = it
                                    },
                                    modifier = Modifier.weight(2f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun LogoutButton(activity: AccountsActivity) {
        val showDialog = remember { mutableStateOf(false) }

        IconButton(
            onClick = {
                showDialog.value = true
            }
        ) {
            Icon(
                painterResource(id = R.drawable.round_logout_24),
                "log out button"
            )
        }

        if (showDialog.value) {
            AlertDialog(
                onDismissRequest = { showDialog.value = false },
                title = {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = "Log out of\nyour account?",
                            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }
                },
                text = { },
                backgroundColor = Color.White,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.size(width = 260.dp, height = 200.dp),
                confirmButton = {
                    TextButton(
                        onClick = {
                            // Stop location tracking
                            fusedLocationClient.removeLocationUpdates(locationCallback)

                            // Sign out from FirebaseAuth
                            FirebaseAuth.getInstance().signOut()

                            // Navigate to LoginActivity
                            val intent = Intent(activity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()

                            // Dismiss the dialog
                            showDialog.value = false
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
                    ) {
                        Text(text = "Log Out", fontWeight = FontWeight.Bold, color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showDialog.value = false },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
                    ) {
                        Text("Cancel")
                    }
                }
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
//            interval = 5 * 60 * 1000 // 5 minutes in milliseconds
//            fastestInterval = 1000
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

    private fun getAddressName(lat: Double, lon: Double): String {
        var addressName = ""
        val geoCoder = Geocoder(this, Locale.getDefault())
        val address = geoCoder.getFromLocation(lat, lon, 1)

        if (address != null) {
            addressName = address[0].getAddressLine(0)
        }
        return addressName
    }
}



