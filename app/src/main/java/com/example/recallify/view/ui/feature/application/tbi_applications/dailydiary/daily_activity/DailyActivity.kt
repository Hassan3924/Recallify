package com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_activity

import android.Manifest
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.recallify.R
import com.example.recallify.data.application.ActivityState
import com.example.recallify.view.common.components.DiaryActivityTopAppBar
import com.example.recallify.view.common.components.ImagePreviewItem
import com.example.recallify.view.ui.theme.RecallifyTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

class DailyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily)
        val dailyActivityCompose: ComposeView =
            findViewById(R.id.activity_daily_diary_daily_activity_screen)
        dailyActivityCompose.setContent {
            RecallifyTheme {
                val viewModel: ActivityViewModel = viewModel()
                DailyActivityScreen(viewModel)
            }
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun DailyActivityScreen(
        activityViewModel: ActivityViewModel? = null
    ) {
        val state = activityViewModel?.state
        val configuration = LocalConfiguration.current

        val screenHeight = configuration.screenHeightDp.dp
        val screenWidth = configuration.screenWidthDp.dp

        val galleryLauncher =
            rememberLauncherForActivityResult(
                ActivityResultContracts.GetMultipleContents()
            ) {
                activityViewModel?.updateSelectedImageList(listOfImages = it)
            }

        val permissionState =
            rememberPermissionState(permission = Manifest.permission.READ_EXTERNAL_STORAGE)

        SideEffect {
            permissionState.launchPermissionRequest()
        }

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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(screenHeight * 0.35f)
                    ) {
                        if (state?.images?.isEmpty() == true) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "No images added!")
                            }
                        }

                        if (state?.images?.isNotEmpty() == true) {
                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.Center)
                            ) {
                                itemsIndexed(state.images) { index, uri ->
                                    ImagePreviewItem(uri = uri,
                                        height = screenHeight * 0.5f,
                                        width = screenWidth * 0.6f,
                                        onClick = { activityViewModel.onItemRemove(index) }
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
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
                    ContentUi(state = state)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {

                            },
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


    @Composable
    fun ContentUi(
        state: ActivityState?
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Start
        ) {
            // Text field for the title
            TextField(
                value = state?.title!!,
                onValueChange = { state.title = it },
                modifier = Modifier.fillMaxWidth(),
                textStyle = MaterialTheme.typography.subtitle2,
                placeholder = { Text(text = "Title") },
                trailingIcon = {
                    IconButton(onClick = {
                        state.title = ""
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.backspace_48),
                            contentDescription = "clear content",
                            modifier = Modifier.size(24.dp),
                            tint = Color.Black
                        )
                    }
                },
                maxLines = 3,
                shape = RoundedCornerShape(6.dp),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.Black,
                )
            )
            Spacer(modifier = Modifier.size(8.dp))
            TextField(
                value = state.description!!,
                onValueChange = { state.description = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                textStyle = MaterialTheme.typography.body2,
                placeholder = { Text(text = "Description") },
                shape = RoundedCornerShape(4.dp),
                colors = TextFieldDefaults.textFieldColors(
                    textColor = Color.Black,
                )
            )
        }
    }
}