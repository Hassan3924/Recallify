package com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_activity

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.example.recallify.view.ui.resource.modules.Response
import com.example.recallify.view.ui.theme.RecallifyTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

class DailyActivity : AppCompatActivity() {

    /**
     * The view-model of Daily Dairy Activity.
     *
     * @since 1.0.0
     *
     * @author enoabasi
     * */
    private val activityViewModel: ActivityViewModel by viewModels()

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

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun DailyActivityScreen() {

        /**
         * @author enoabasi
         * */
        val state = activityViewModel.state

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
                ActivityResultContracts.GetMultipleContents()
            ) { imageUri ->
                imageUri.let {
                    activityViewModel.updateSelectedImageList(listOfImages = it)
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
            scaffoldState = rememberScaffoldState(),
            topBar = { DiaryActivityTopAppBar() },
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
                    /**
                     * Image selection and loading of the data from the user's mobile
                     * The list can carry only 10 images to save space on the database.
                     *
                     * The number of images in the list is 10.
                     * */
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(screenHeight * 0.35f)
                    ) {
                        if (state.images.isEmpty()) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "No images added!")
                            }
                        }

                        if (state.images.isNotEmpty()) {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.Center)
                            ) {
                                itemsIndexed(state.images) { index, uri ->
                                    if (uri != null) {
                                        ImagePreviewItem(uri = uri,
                                            height = screenHeight * 0.5f,
                                            width = screenWidth * 0.6f,
                                            onClick = { activityViewModel.onItemRemove(index) }
                                        )
                                    } else {
                                        Text(text = "no image to display")
                                    }
                                    Spacer(modifier = Modifier.width(3.dp))
                                }
                            }
                        }
                    }
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(onClick = {
                            if (permissionState.status.isGranted) {
                                galleryLauncher.launch("image/*")
                            } else
                                permissionState.launchPermissionRequest()
                        }) {
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
                            value = state.title ?: "",
                            onValueChange = { activityViewModel.onTitleChange(it) },
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = MaterialTheme.typography.subtitle2.copy(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            placeholder = { Text(text = "Title") },
                            trailingIcon = {
                                IconButton(onClick = {
                                    state.title ?: ""
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
                            value = state.description ?: "",
                            onValueChange = { activityViewModel.onDescriptionChange(it) },
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
                            )
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
                                    text = "You are about to leave daily activity without " +
                                            "making an activity.\nAre you sure you want to " +
                                            "continue."
                                )
                            },
                            shape = RoundedCornerShape(5.dp),
                            backgroundColor = White
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
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
                                when (val addImageToStorageResponse =
                                    activityViewModel.addImageToStorageResponse) {
                                    is Response.Loading -> {}
                                    is Response.Success -> {
                                        val imageList = listOf(addImageToStorageResponse.data)
                                        activityViewModel.createActivity(
                                            scaffoldState = scaffoldState,
                                            imageUri = imageList
                                        )
                                    }
                                    is Response.Failure -> {
                                        Response.Failure(addImageToStorageResponse.message)
                                    }
                                    else -> {
                                        Log.w(
                                            "MYTAG",
                                            "Error on retrieving the data from the database"
                                        )
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