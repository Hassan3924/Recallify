package com.example.recallify.view.ui.feature.application.tbi_applications.tbimainsettings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
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
import androidx.core.content.ContextCompat
import com.example.recallify.R
import com.example.recallify.view.ui.feature.guradian_application.mainsettingpages.GuardianMainSettings
import com.example.recallify.view.ui.resource.controller.BottomBarFiller
import com.example.recallify.view.ui.theme.RecallifyTheme

class PrivacyAndSecurityTBI : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_privacy_security)

        checkPermissions()

        if (!allPermissionsGranted()) {
            requestPermissions()
        }

        val privacySecurity: ComposeView = findViewById(R.id.privacy_security)

        privacySecurity.setContent {
            RecallifyTheme {
                PrivacyAndSecurityScreen()
            }
        }
    }


    private val locationPermission = Manifest.permission.ACCESS_FINE_LOCATION
    private val cameraPermission = Manifest.permission.CAMERA
    private val microphonePermission = Manifest.permission.RECORD_AUDIO
    private val bluetoothPermission = Manifest.permission.BLUETOOTH_CONNECT
    private val notificationPermission = Manifest.permission.POST_NOTIFICATIONS

    private val permissionRequestCode = 101

    private var locationPermissionGranted = false
    private var cameraPermissionGranted = false
    private var microphonePermissionGranted = false
    private var bluetoothPermissionGranted = false
    private var notificationPermissionGranted = false

    @Composable
    fun PrivacyAndSecurityScreen() {

        Scaffold(
            topBar = { PrivacySecurityTopBar() },
            bottomBar = { BottomBarFiller() },
            backgroundColor = MaterialTheme.colors.surface
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .padding(top = 4.dp)
                        .padding(bottom = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {


                }
            }
        }
    }

    @Composable
    private fun PrivacySecurityTopBar() {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
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
                    text = "Privacy & Security",
                    style = MaterialTheme.typography.body1.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.weight(2f)
                )
            }
        }
    }


    private fun checkPermissions() {
        locationPermissionGranted = ContextCompat.checkSelfPermission(
            this, locationPermission) == PackageManager.PERMISSION_GRANTED
        cameraPermissionGranted = ContextCompat.checkSelfPermission(
            this, cameraPermission) == PackageManager.PERMISSION_GRANTED
        microphonePermissionGranted = ContextCompat.checkSelfPermission(
            this, microphonePermission) == PackageManager.PERMISSION_GRANTED
        bluetoothPermissionGranted = ContextCompat.checkSelfPermission(
            this, bluetoothPermission) == PackageManager.PERMISSION_GRANTED
        notificationPermissionGranted = ContextCompat.checkSelfPermission(
            this, notificationPermission) == PackageManager.PERMISSION_GRANTED
    }

    private fun allPermissionsGranted() = locationPermissionGranted &&
            cameraPermissionGranted &&
            microphonePermissionGranted &&
            bluetoothPermissionGranted &&
            notificationPermissionGranted

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                locationPermission,
                cameraPermission,
                microphonePermission,
                bluetoothPermission,
                notificationPermission
            ),
            permissionRequestCode
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == permissionRequestCode) {
            // Check if all permissions are granted
            checkPermissions()

            // Use the permissions as needed
            // ...

            // Handle denied permissions
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    // User denied the permission
                    // ...
                }
            }
        }
    }
}