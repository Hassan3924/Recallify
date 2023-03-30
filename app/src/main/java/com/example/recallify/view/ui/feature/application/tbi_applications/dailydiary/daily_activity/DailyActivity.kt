package com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_activity

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recallify.R
import com.example.recallify.view.common.components.ImagePreviewItem
import com.example.recallify.view.common.resources.DiaryActivityTopAppBar
import com.example.recallify.view.ui.feature.application.tbi_applications.accounts.copiedLatitude
import com.example.recallify.view.ui.feature.application.tbi_applications.accounts.copiedLocation
import com.example.recallify.view.ui.feature.application.tbi_applications.accounts.copiedLongitude
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.DailyDiaryActivity
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_log.screens.getCurrentDate
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.moment_snap.copideImageLink
import com.example.recallify.view.ui.theme.RecallifyTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DailyActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    private val current = LocalDateTime.now()

    @RequiresApi(Build.VERSION_CODES.O)
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm a")

    @RequiresApi(Build.VERSION_CODES.O)
    private val timeFormatted = current.format(timeFormatter)

    @RequiresApi(Build.VERSION_CODES.O)
    private val currentTime = timeFormatted.toString()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily)
        val dailyActivityCompose: ComposeView =
            findViewById(R.id.activity_daily_diary_daily_activity_screen)
        dailyActivityCompose.setContent {
            RecallifyTheme {
                DailyActivityScreen()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun DailyActivityScreen() {

        val imageLink: MutableState<Uri?> = remember {
            mutableStateOf(copideImageLink.value)
        }

        val location = remember {
            mutableStateOf("")
        }

        var isLoading by remember {
            mutableStateOf(false)
        }

        /**
         * @author enoabasi
         * */
        val configuration = LocalConfiguration.current

        /**
         * @author enoabasi
         * */
        val screenHeight = configuration.screenHeightDp.dp

        /**
         * @author enoabasi
         * */
        val screenWidth = configuration.screenWidthDp.dp

        /**
         * @author enoabasi
         * */
        val galleryLauncher =
            rememberLauncherForActivityResult(
                ActivityResultContracts.GetContent()
            ) { imageUri ->
                imageUri.let {
                    imageLink.value = it
                }
            }

        /**
         * The given permission to access the directory for photos and files on a user's device.
         * It is fired through a side effect that pauses the application and reads fpr result change
         * such as adding an image to te image list.
         *
         * @author enoabasi
         * */
        val permissionState =
            rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)

        SideEffect {
            permissionState.launchPermissionRequest()
        }

        /**
         * @author enoabasi
         * */
        val scaffoldState = rememberScaffoldState()

        val scope = rememberCoroutineScope()

        /**
         * The dialog receiver for posting a log to the firebase console
         * @author enoabasi
         */
        var cancelDialog by remember {
            mutableStateOf(false)
        }

        /**
         * The local context of the application package. This could have been substituted for
         * ```@this.DailyActivity::class.java``` but the Local context retrieves the context
         * of the whole application at the current stage.
         *
         * @author enoabasi
         * */
        val context = LocalContext.current

        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                DiaryActivityTopAppBar {
                    IconButton(onClick = {
                        context.startActivity(Intent(context, DailyDiaryActivity::class.java))
                        finish()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_arrow_back_24),
                            contentDescription = "back to home"
                        )
                    }
                }
            },
            backgroundColor = MaterialTheme.colors.surface
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues = paddingValues),
                verticalArrangement = Arrangement.Top
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 3.dp)
                        .padding(bottom = 8.dp)
                ) {
                    Spacer(modifier = Modifier.size(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(screenHeight * 0.4f)
                            .width(screenWidth * 0.8f)
                            .padding(2.dp)
                    ) {
                        if (imageLink.value != null) {
                            ImagePreviewItem(
                                uri = imageLink.value!!,
                                height = screenHeight * 0.5f,
                                width = screenWidth * 0.9f
                            ) {
                                imageLink.value = null
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .border(
                                        border = BorderStroke(2.dp, Black),
                                        shape = RoundedCornerShape(6.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "No image added!",
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.padding(vertical = 4.dp))
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        OutlinedButton(
                                            onClick = {
                                                if (permissionState.status.isGranted) {
                                                    galleryLauncher.launch("image/*")
                                                } else
                                                    permissionState.launchPermissionRequest()
                                            },
                                            border = BorderStroke(
                                                2.dp,
                                                MaterialTheme.colors.onSurface
                                            )
                                        ) {
                                            Text(
                                                text = "Add image",
                                                style = MaterialTheme.typography.button.copy(
                                                    color = MaterialTheme.colors.onSurface
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.padding(vertical = 10.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        OutlinedTextField(
                            value = location.value,
                            onValueChange = { location.value = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            textStyle = MaterialTheme.typography.h3.copy(
                                fontSize = 22.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            label = { Text(text = "Please enter location") },
                            placeholder = { Text(text = "Example: Place, City, Country") },
                            singleLine = false,
                            maxLines = 3,
                            shape = RoundedCornerShape(6.dp),
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = Black,
                                backgroundColor = MaterialTheme.colors.surface
                            )
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                    }

                    if (cancelDialog) {
                        AlertDialog(
                            onDismissRequest = { cancelDialog = false },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        context.startActivity(
                                            Intent(
                                                context,
                                                DailyDiaryActivity::class.java
                                            )
                                        )
                                        finish()
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
                                    text = "Discarding Activity!",
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
                                    text = "Are you sure you want to discard activity?",
                                    style = MaterialTheme.typography.body1
                                )
                            },
                            shape = MaterialTheme.shapes.medium,
                            backgroundColor = White
                        )
                    }


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isLoading) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.Start
                            ) {
                                LinearProgressIndicator()
                                Text(text = "Finishing up activity. Please wait...")
                            }
                        }
                        Button(
                            onClick = { cancelDialog = true },
                            modifier = Modifier.padding(4.dp),
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.onError,
                                contentColor = MaterialTheme.colors.error,
                            )
                        ) {
                            Text(text = "Cancel", style = MaterialTheme.typography.button)
                        }
                        Button(
                            onClick = {
                                isLoading = true
                                scope.launch {
                                    val database = FirebaseDatabase.getInstance().reference
                                    val storage = FirebaseStorage.getInstance().reference

                                    val userID = FirebaseAuth.getInstance().currentUser?.uid!!

                                    val allActivities = database
                                        .child("users")
                                        .child(userID)
                                        .child("allActivities")

                                    val activityRef = database
                                        .child("users")
                                        .child(userID)
                                        .child("dailyDairyDummy")
                                        .child(getCurrentDate())

                                    val key = activityRef.push().key!!

                                    val activityImage = storage
                                        .child("imageFolder")
                                        .child(userID)
                                        .child(getCurrentDate())
                                        .child(key)
                                    if ((imageLink.value != null) && location.value.isNotEmpty() && location.value.length >= 6) {
                                        imageLink.let { link ->
                                            activityImage.putFile(link.value!!)
                                                .addOnCompleteListener { task ->
                                                    if (task.isSuccessful) {
                                                        activityImage.downloadUrl.addOnSuccessListener { uri ->
                                                            activityRef.child(key)
                                                                .child("userId")
                                                                .setValue(userID)
                                                            activityRef.child(key)
                                                                .child("activityId")
                                                                .setValue(key)
                                                            allActivities.child(key)
                                                                .child("activityId")
                                                                .setValue(key)
                                                            activityRef.child(key)
                                                                .child("imageLink")
                                                                .setValue(uri.toString())
                                                            allActivities.child(key)
                                                                .child("imageLink")
                                                                .setValue(uri.toString())
                                                            activityRef.child(key)
                                                                .child("locationName")
                                                                .setValue(location.value)
                                                            allActivities.child(key)
                                                                .child("locationName")
                                                                .setValue(location.value)
                                                            activityRef.child(key)
                                                                .child("time")
                                                                .setValue(currentTime)
                                                            allActivities.child(key)
                                                                .child("time")
                                                                .setValue(currentTime)
                                                            activityRef.child(key)
                                                                .child("date")
                                                                .setValue(getCurrentDate())
                                                            allActivities.child(key)
                                                                .child("date")
                                                                .setValue(getCurrentDate())
                                                            activityRef.child(key)
                                                                .child("locationAddress")
                                                                .setValue(copiedLocation.value)
                                                            allActivities.child(key)
                                                                .child("locationAddress")
                                                                .setValue(copiedLocation.value)
                                                            activityRef.child(key)
                                                                .child("locationLatitude")
                                                                .setValue(copiedLatitude.value)
                                                            allActivities.child(key)
                                                                .child("locationLatitude")
                                                                .setValue(copiedLatitude.value)
                                                            activityRef.child(key)
                                                                .child("locationLongitude")
                                                                .setValue(copiedLongitude.value)
                                                            allActivities.child(key)
                                                                .child("locationLongitude")
                                                                .setValue(copiedLongitude.value)
                                                        }.addOnCompleteListener {
                                                            isLoading = false
                                                            Toast.makeText(
                                                                context,
                                                                "Activity Posted! 👍",
                                                                Toast.LENGTH_SHORT
                                                            ).show()
                                                            context.startActivity(
                                                                Intent(
                                                                    context,
                                                                    DailyDiaryActivity::class.java
                                                                )
                                                            )
                                                            finish()
                                                        }
                                                    } else {
                                                        Toast.makeText(
                                                            context,
                                                            task.exception?.message,
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                        }
                                    } else {
                                        isLoading = false
                                        Toast.makeText(
                                            context,
                                            "There needs to be an Image, Location and location " +
                                                    "needs to be more descriptive.",
                                            Toast.LENGTH_LONG
                                        ).show()
                                    }

                                }
                            },
                            modifier = Modifier.padding(4.dp),
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.primary,
                                contentColor = MaterialTheme.colors.onPrimary,
                            )
                        ) {
                            Text(text = "Post", style = MaterialTheme.typography.button)
                        }
                    }
                }
            }
        }
    }
}