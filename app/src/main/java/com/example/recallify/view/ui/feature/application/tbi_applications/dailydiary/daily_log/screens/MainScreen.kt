package com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_log.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_log.MainViewModel
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_log.SpeechRecognitionContract
import com.example.speech_to_text_jetpack.navigation.AudioScreens
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val reference: DatabaseReference = database.reference.child("users")
    val auth: FirebaseAuth = Firebase.auth
    val currentUser = auth.currentUser?.uid!!
    val currentDate = getCurrentDate().replace("-", "_")
    reference.child(currentUser).child("daily-diary-recordings").child(currentDate)
    reference.child(currentUser).child("daily-diary-recordings")
        .child(getCurrentDate())

    val permissionState = rememberPermissionState(
        permission = android.Manifest.permission.RECORD_AUDIO
    )
    SideEffect {
        permissionState.launchPermissionRequest()
    }

    val speechRecognizerLauncher = rememberLauncherForActivityResult(

        contract = SpeechRecognitionContract(),
        onResult = {

            viewModel.changeTextValue(it.toString())

            navController.navigate(AudioScreens.AudioLogScreen.name)

        }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = Color.Green
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 5.dp),
                    text = "Daily Audio Log"
                )
            }
        }

    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight()
                .padding(paddingValues = it),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    modifier = Modifier
                        .background(Color.Transparent),
                    text = "CREATE",
                    style = TextStyle(fontSize = 50.sp)
                )
                Text(
                    modifier = Modifier
                        .background(Color.Transparent),
                    text = "A",
                    style = TextStyle(fontSize = 50.sp)
                )

                Text(
                    modifier = Modifier
                        .background(Color.Transparent),
                    text = "LOG",
                    style = TextStyle(fontSize = 50.sp)
                )

                Box(
                    modifier = Modifier
                        .padding(top = 50.dp)
                        .clip(CircleShape)
                        .size(100.dp)
                        .background(Color.White)
                ) {

                    IconButton(
                        modifier = Modifier.align(Alignment.Center),
                        onClick = {
                            if (permissionState.status.isGranted) {
                                speechRecognizerLauncher.launch(Unit)
                            }
                        }
                    )
                    {

                        Box {
                            Icon(
                                Icons.Default.Call,
                                contentDescription = "Microphone",
                                modifier = Modifier
                                    .size(50.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}


fun getCurrentDate(): String {

    val date = Date().time
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(date)

}