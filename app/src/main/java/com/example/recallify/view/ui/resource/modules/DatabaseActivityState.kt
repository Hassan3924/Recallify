package com.example.recallify.view.ui.resource.modules

import com.example.recallify.data.application.ActivityFromDatabase

sealed class DatabaseActivityState {
    class Success(val data: MutableList<ActivityFromDatabase>) : DatabaseActivityState()
    class Failure(val message : String) : DatabaseActivityState()
    object Loading: DatabaseActivityState()
    object Empty : DatabaseActivityState()
}
