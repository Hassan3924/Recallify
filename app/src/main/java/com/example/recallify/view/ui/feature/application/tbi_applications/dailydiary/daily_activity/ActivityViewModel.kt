package com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_activity

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recallify.data.application.ActivityState
import kotlinx.coroutines.launch

class ActivityViewModel : ViewModel() {

    var state by mutableStateOf(ActivityState())
        private set

    fun updateSelectedImageList(listOfImages: List<Uri>) {
        val updatedImageList = state.images?.toMutableList()
        viewModelScope.launch {
            if (updatedImageList != null) {
                updatedImageList += listOfImages
            }
            if (updatedImageList != null) {
                state = state.copy(
                    images = updatedImageList.distinct()
                )
            }
        }
    }

    fun onItemRemove(index: Int) {
        val updatedImageList = state.images?.toMutableList()
        viewModelScope.launch {
            updatedImageList?.removeAt(index)
            state = state.copy(
                images = updatedImageList?.distinct()
            )
        }
    }

}