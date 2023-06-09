package com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.moment_snap

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.example.recallify.R
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.DailyDiaryActivity
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_activity.DailyActivity
import com.example.recallify.view.ui.theme.RecallifyTheme
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

var copideImageLink: MutableState<Uri?> = mutableStateOf(null)

class MomentSnapActivity : AppCompatActivity() {

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)

    /**
     * Request the permission to use the camera in our application.
     * @author enoabasi
     * */
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.i("kilo", "permission granted")
            shouldShowCamera.value = true
        } else {
            Log.i("kilo", "Permission denied!")
        }
    }

    private lateinit var photoUri: Uri
    private var shouldShowPhoto: MutableState<Boolean> = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_moment_snap)
        val momentSnapCompose: ComposeView =
            findViewById(R.id.activity_daily_diary_moment_snap_screen)
        momentSnapCompose.setContent {
            RecallifyTheme {
                if (shouldShowCamera.value) {
                    CameraView(
                        outputDirectory = outputDirectory,
                        executor = cameraExecutor,
                        onImageCaptured = ::handleImageCapture,
                        onError = { Log.e("kilo", "View error:", it) }
                    )
                }

                if (shouldShowPhoto.value) {
                    var cancelDialog by remember {
                        mutableStateOf(false)
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = "Moment Snap preview",
                            style = MaterialTheme.typography.h6.copy(
                                color = MaterialTheme.colors.onSurface
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )
                        Text(
                            text = "This image can be used to create an activity. Let's go!! 🚀",
                            style = MaterialTheme.typography.body2.copy(
                                color = Color.Gray
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )
                        Image(
                            painter = rememberAsyncImagePainter(photoUri),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (cancelDialog) {
                                AlertDialog(
                                    onDismissRequest = { cancelDialog = false },
                                    confirmButton = {
                                        TextButton(
                                            onClick = {
                                                val intent = Intent(
                                                    this@MomentSnapActivity,
                                                    DailyDiaryActivity::class.java
                                                )
                                                startActivity(intent)
                                            }
                                        ) {
                                            Text(
                                                text = "Discard",
                                                style = MaterialTheme.typography.button.copy(
                                                    color = MaterialTheme.colors.error,
                                                    fontWeight = FontWeight.Medium
                                                ),
                                            )
                                        }
                                    },
                                    dismissButton = {
                                        TextButton(
                                            onClick = { cancelDialog = false }
                                        ) {
                                            Text(
                                                "Cancel",
                                                style = MaterialTheme.typography.button.copy(
                                                    color = MaterialTheme.colors.onBackground,
                                                    fontWeight = FontWeight.Medium
                                                ),
                                            )
                                        }
                                    },
                                    title = {
                                        Text(
                                            text = "Discarding Snap!",
                                            style = MaterialTheme.typography.h6.copy(
                                                fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier.padding(
                                                vertical = 8.dp
                                            )
                                        )
                                    },
                                    text = {
                                        Text(
                                            text = "Are you sure you want to discard the moment?",
                                            style = MaterialTheme.typography.body1
                                        )
                                    },
                                    shape = MaterialTheme.shapes.medium,
                                    backgroundColor = Color.White
                                )
                            }
                            Button(
                                onClick = {
                                    cancelDialog = true
                                },
                                modifier = Modifier
                                    .padding(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    backgroundColor = MaterialTheme.colors.onError,
                                    contentColor = MaterialTheme.colors.error,
                                )
                            ) {
                                Text(
                                    text = "Discard",
                                    style = MaterialTheme.typography.button
                                )
                            }
                            Button(
                                onClick = {
                                    copideImageLink.value = photoUri
                                    val intent =
                                        Intent(
                                            this@MomentSnapActivity,
                                            DailyActivity::class.java
                                        )
                                    startActivity(intent)
                                    finish()
                                },
                                modifier = Modifier
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "Create Activity",
                                    style = MaterialTheme.typography.button
                                )
                            }
                        }
                    }
                }
            }
        }

        requestCameraPermission()

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i("kilo", "Permission is already granted!")
                shouldShowCamera.value = true
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

    private fun handleImageCapture(uri: Uri) {
        Log.i("kilo", "Image captured: $uri")
        shouldShowCamera.value = false
        photoUri = uri
        shouldShowPhoto.value = true
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}