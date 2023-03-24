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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.recallify.R
import com.example.recallify.view.common.components.DiaryActivityTopAppBar
import com.example.recallify.view.common.components.ImagePreviewItem
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.DailyDiaryActivity
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_log.screens.getCurrentDate
import com.example.recallify.view.ui.feature.application.tbi_applications.dashboard.copiedLocation
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
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")

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
            mutableStateOf(null)
        }

        val title = remember {
            mutableStateOf("")
        }

        val description = remember {
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
                DiaryActivityTopAppBar(context) {
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
                verticalArrangement = Arrangement.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 4.dp)
                        .padding(bottom = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(screenHeight * 0.35f)
                            .width(screenWidth * 0.3f)
                            .padding(2.dp)
                    ) {
                        if (imageLink.value != null) {
                            ImagePreviewItem(
                                uri = imageLink.value!!,
                                height = screenHeight * 0.5f,
                                width = screenWidth * 0.6f
                            ) {
                                imageLink.value = null
                            }
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "No images added!")
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.End
                    ) {
                        Button(onClick = {
                            if (permissionState.status.isGranted) {
                                galleryLauncher.launch("image/*")
                            } else
                                permissionState.launchPermissionRequest()
                        }
                        ) {
                            Text(text = "Add images")
                        }
                    }
                    /**
                     * Functionality for adding title to the post and a description
                     * The user can add a title of maximum 150 characters and for the
                     * description a maximum of 500 characters.
                     *
                     * The location, date, and time will be displayed automatically
                     * by background functions. The values are stored in a data class
                     * and sent to the firebase.reference.getInstance().
                     * */
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        /**
                         * Text-field of the title:
                         * A title will be able to have maximum 150 characters and
                         * will be the identifier of the post itself. The title
                         * cannot be empty else a post will not be granted. In
                         * other terms the title can be deemed as a caption to
                         * the post.
                         *
                         * @author essien
                         * */
                        TextField(
                            value = title.value,
                            onValueChange = { title.value = it },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = MaterialTheme.typography.subtitle2.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            placeholder = { Text(text = "Title") },
                            trailingIcon = {
                                IconButton(onClick = {
                                    title.value = ""
                                }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.backspace_48),
                                        contentDescription = "clear content",
                                        modifier = Modifier.size(24.dp),
                                        tint = Color.Black
                                    )
                                }
                            },
                            singleLine = false,
                            maxLines = 3,
                            shape = RoundedCornerShape(6.dp),
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = Color.Black,
                            )
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        /**
                         * Text-field of the description:
                         * A description will be able to have maximum 500 characters and
                         * will be the description of an event. The description can be
                         * empty as it is not an important part of the post information.
                         *
                         * @author essien
                         * */
                        TextField(
                            value = description.value,
                            onValueChange = { description.value = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            textStyle = MaterialTheme.typography.body2.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            placeholder = { Text(text = "Description") },
                            shape = RoundedCornerShape(4.dp),
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = Color.Black,
                            ),
                            maxLines = 30
                        )
                    }
                    /**
                     * Post actions: Cancel and Post
                     * Cancel - To discard and delete a post that is being edited or created.
                     *          This does not affect the database or any other component on the main
                     *          screen.
                     * Post - To save amd post an activity to the main screen as well as the
                     *        database. This affects the main screen as it will be re-composed
                     *        with updated data/changes.
                     * */
                    if (cancelDialog) {
                        AlertDialog(
                            onDismissRequest = { cancelDialog = false },
                            confirmButton = {
                                TextButton(onClick = { cancelDialog = false }
                                ) { Text(text = "cancel") }
                            },
                            dismissButton = {
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
                                    Text(text = "Discard")
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            title = { Text(text = "Cancel editing") },
                            text = {
                                Text(
                                    text = "Leaving without making an activity.\n" +
                                            "Are you sure you want to continue?"
                                )
                            },
                            shape = RoundedCornerShape(5.dp),
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
                                Text(text = "finishing up activity...")
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
                            Text(text = "cancel")
                        }
                        Button(
                            onClick = {
                                isLoading = true
                                scope.launch {
                                    val database = FirebaseDatabase.getInstance().reference
                                    val storage = FirebaseStorage.getInstance().reference

                                    val userID = FirebaseAuth.getInstance().currentUser?.uid!!

                                    var activityId = 1

                                    val activityImage = storage
                                        .child("imageFolder")
                                        .child(userID)
                                        .child(getCurrentDate())
                                        .child(activityId.toString())

                                    val activityRef = database
                                        .child("users")
                                        .child(userID)
                                        .child("dailyDairyDummy")
                                        .child(getCurrentDate())

                                    val key = activityRef.push().key!!

//                                    val fetchKey = activityRef.key!!
//
//                                    val fetchDB = activityRef.child(fetchKey)

                                    imageLink.let { link ->
                                        activityImage.putFile(link.value!!)
                                            .addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                    activityImage.downloadUrl.addOnSuccessListener { uri ->
//                                                        activityRef.child(key)
//                                                            .child("userId")
//                                                            .setValue(userID)
//                                                        activityRef.child(activityId.toString())
//                                                            .child("activityId")
//                                                            .setValue(activityId)
//                                                        activityRef.child(activityId.toString())
//                                                            .child("imageLink")
//                                                            .setValue(uri.toString())
//                                                        activityRef.child(activityId.toString())
//                                                            .child("title")
//                                                            .setValue(title.value)
//                                                        activityRef.child(activityId.toString())
//                                                            .child("description")
//                                                            .setValue(description.value)
//                                                        activityRef.child(activityId.toString())
//                                                            .child("time")
//                                                            .setValue(currentTime)
//                                                        activityRef.child(activityId.toString())
//                                                            .child("date")
//                                                            .setValue(getCurrentDate())
//                                                        activityRef.child(activityId.toString())
//                                                            .child("location")
//                                                            .setValue(copiedLocation.value)
                                                        activityRef.child(key)
                                                            .child("userId")
                                                            .setValue(userID)
                                                        activityRef.child(key)
                                                            .child("activityId")
                                                            .setValue(key)
                                                        activityRef.child(key)
                                                            .child("imageLink")
                                                            .setValue(uri.toString())
                                                        activityRef.child(key)
                                                            .child("title")
                                                            .setValue(title.value)
                                                        activityRef.child(key)
                                                            .child("description")
                                                            .setValue(description.value)
                                                        activityRef.child(key)
                                                            .child("time")
                                                            .setValue(currentTime)
                                                        activityRef.child(key)
                                                            .child("date")
                                                            .setValue(getCurrentDate())
                                                        activityRef.child(key)
                                                            .child("location")
                                                            .setValue(copiedLocation.value)
                                                        activityId++
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
                                }
                            },
                            modifier = Modifier.padding(4.dp),
                            shape = RoundedCornerShape(4.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = MaterialTheme.colors.primary,
                                contentColor = MaterialTheme.colors.onPrimary,
                            )
                        ) {
                            Text(text = "post")
                        }
                    }
                }
            }
        }
    }
}