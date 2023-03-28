package com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.moment_snap

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.Text
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.recallify.R
import com.example.recallify.view.ui.theme.RecallifyTheme

class MomentSnapActivity : AppCompatActivity() {

    /**
     * Request the permission to use the camera in our application.
     * @author enoabasi
     * */
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.i("kilo", "permission granted")
        } else {
            Log.i("kilo", "Permission denied!")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_moment_snap)
        val momentSnapCompose: ComposeView =
            findViewById(R.id.activity_daily_diary_moment_snap_screen)
        momentSnapCompose.setContent {
            RecallifyTheme {
                Text(text = "hello moment snap!")
            }
        }

        requestCameraPermission()
    }

    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i("kilo", "Permission is already granted!")
            }
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            ) -> {
                Log.i("kilo", "show camera permission dialog!")
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }
}