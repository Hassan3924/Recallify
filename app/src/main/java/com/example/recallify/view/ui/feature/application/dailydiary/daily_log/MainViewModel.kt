package com.example.recallify.view.ui.feature.application.dailydiary.daily_log

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel : ViewModel() {
    private var counter: Int = 1

    private fun getCurrentDate(): String {

        val date = Date().time
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return formatter.format(date)

    }

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val reference: DatabaseReference = database.reference.child("users")
    private val auth: FirebaseAuth = Firebase.auth
    private val currentUser = auth.currentUser?.uid!!

    private var state by mutableStateOf(MainScreenState())

    fun changeTextValue(text: String) {

        var modifiedText: String

        viewModelScope.launch {

            state = state.copy(
                text = text
            )

            modifiedText = text.replace("[", "").replace("]", "")
            reference.child(currentUser).child("daily-diary-recordings").child(getCurrentDate())
                .child(counter.toString()).setValue(modifiedText)
            counter += 1

        }


    }

}

