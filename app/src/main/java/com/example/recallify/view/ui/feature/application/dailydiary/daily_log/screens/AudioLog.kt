package com.example.recallify.view.ui.feature.application.dailydiary.daily_log.screens

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.date.datepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.mutableStateOf

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun datePicker(): String {

    val context = LocalContext.current

    var pickedDate by remember {
        mutableStateOf(LocalDate.now())
    }

    val formattedDate by remember {
        derivedStateOf {
            DateTimeFormatter
                .ofPattern("MM dd yyyy")
                .format(pickedDate)
        }
    }

    val dateDialogState = rememberMaterialDialogState()

    Button(onClick = {
        dateDialogState.show()
    }) {
        Text(text = "Pick a Date")
    }
//    Text(text = formattedDate)


    MaterialDialog(

        dialogState = dateDialogState,
        buttons = {
            positiveButton(text = "Ok") {
                Toast.makeText(
                    context,
                    "Clicked Ok",
                    Toast.LENGTH_LONG
                ).show()
            }
            negativeButton(
                text = "Cancel"
            )

        }
    ) {
        datepicker(

            initialDate = LocalDate.now(),
            title = "Pick a Date"

        ) {
            pickedDate = it
        }
    }

    return pickedDate.toString()

}

@Composable
fun RecordRow(movie: String) {

    Card(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .height(130.dp),
        shape = RoundedCornerShape(corner = CornerSize(16.dp)),
        elevation = 6.dp
    ) {
        Text(modifier = Modifier.padding(10.dp), text = movie)
    }

}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AudioLogScreen(navController: NavController) {
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    val reference: DatabaseReference = database.reference.child("users")
    val auth: FirebaseAuth = Firebase.auth
    val currentUser = auth.currentUser?.uid!!
    remember { mutableStateOf(LocalDate.now()) }
    var childValues by remember { mutableStateOf(listOf<String>()) }

    Scaffold(
        topBar = {
            TopAppBar(backgroundColor = Color.Green) {
                Text(text = "Log Record")
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(top = 20.dp, bottom = 20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val datePicked = datePicker()

            val recordString =
                reference.child(currentUser).child("daily-diary-recordings").child(datePicked)
            recordString.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = snapshot.value as? ArrayList<*>
                        ?: return // Check if the value is an ArrayList
                    childValues = list.filterIsInstance<String>() // Filter out non-string values
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("AudioLogScreen", "Error retrieving data from database: ${error.message}")
                }
            })

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(childValues) { value ->
                    RecordRow(
                        value
                    )
                }
            }
        }
    }
}


