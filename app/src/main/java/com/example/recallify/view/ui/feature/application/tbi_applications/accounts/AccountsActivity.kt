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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.example.recallify.R
import com.example.recallify.view.ui.feature.application.tbi_applications.tbimainsettings.MainSettingsTBI
import com.example.recallify.view.ui.feature.security.signin.LoginActivity
import com.example.recallify.view.ui.resource.controller.BottomBarFiller
import com.example.recallify.view.ui.theme.RecallifyTheme
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
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


val copiedLocation = mutableStateOf("")
val copiedLongitude = mutableStateOf("")
val copiedLatitude =  mutableStateOf("")
class AccountsActivity : AppCompatActivity() {

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
                val address = getAddressName(location.latitude,location.longitude)
                copiedLocation.value = address
                copiedLatitude.value = lat.toString()
                copiedLongitude.value = lng.toString()
                Log.d("CopiedLocation: ", "${copiedLocation.value} ==> $address")
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
        val childValue = remember { mutableStateOf("") }

            Scaffold(
                topBar = {AccountSettingsTopBar()} ,
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
                        .padding(top = 4.dp)
                        .padding(bottom = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Icon(
                        Icons.Filled.Person,
                        contentDescription = "User icon",
                        tint = Color.Gray,
                        modifier = Modifier.size(150.dp)
                    )

//                    Text(style = TextStyle(fontSize = 24.sp), text = "Account Settings")

                }
                Column( modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .padding(top = 50.dp)
                    .padding(bottom = 8.dp),
                    Arrangement.Center,
                    Alignment.CenterHorizontally) {

                    var firstName: String by remember { mutableStateOf("") }
                    var lastName: String by remember { mutableStateOf("") }
                    var email: String by remember { mutableStateOf("") }
                    var password: String by remember { mutableStateOf("") }
                    var PIN : String by remember { mutableStateOf("") }
//                    var password by rememberSaveable { mutableStateOf("") }


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
                                    lastName = dataSnapshot.getValue(String::class.java) ?:""
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
                        modifier = Modifier.padding(horizontal = 30.dp, vertical = 10.dp),
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
                        modifier = Modifier.padding(horizontal = 30.dp, vertical = 10.dp)
                    ) {

                        Text(text = "Last Name:")
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = lastName)
                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 30.dp, vertical = 10.dp)
                    ) {
                        Text(text = "Email:")
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = email)

                    }

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 30.dp, vertical = 10.dp)
                    ) {

                        Text(text = "PIN:")
                        Spacer(modifier = Modifier.weight(1f))
                        Text(text = PIN)

                    }
                    LogoutButton(activity = this@AccountsActivity)
                }
            }
        }
    }

    @Composable
    fun LogoutButton(activity: AccountsActivity) {
        Button(modifier = Modifier.padding(top = 20.dp), onClick = {
              fusedLocationClient.removeLocationUpdates(locationCallback) //to stop the location tracking
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this@AccountsActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }) {
            Text(text = "Log Out")
        }
    }

    @Composable
    private fun AccountSettingsTopBar() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp)
                .padding(top = 4.dp)
                .clip(shape = RoundedCornerShape(26.dp))
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
                IconButton(
                    onClick = {
                        val intent = Intent(
                            applicationContext,
                            MainSettingsTBI::class.java
                        )
                        startActivity(intent)
                        overridePendingTransition(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                        finish()
                    },
                    Modifier.weight(1f)

                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.round_arrow_back_24),
                        contentDescription = "Go back to Main Settings",
                        modifier = Modifier
                            .size(42.dp)
                            .border(
                                border = BorderStroke(2.dp, SolidColor(Color.Black)),
                                shape = RoundedCornerShape(20.dp)
                            )
                    )
                }
                Text(
                    text = "Account Settings",
                    style = MaterialTheme.typography.body1.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.weight(2f)
                )
            }
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
//            interval = LOCATION_UPDATE_INTERVAL
//            fastestInterval = FASTEST_LOCATION_UPDATE_INTERVAL
            interval = 5 * 60 * 1000 // 5 minutes in milliseconds
            fastestInterval = 1000
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



