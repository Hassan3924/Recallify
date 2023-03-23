package com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_activity

import android.net.Uri
import android.util.Log
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarDuration
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.recallify.data.application.ActivityState
import com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_log.screens.getCurrentDate
import com.example.recallify.view.ui.resource.modules.ActivityDataState
import com.example.recallify.view.ui.resource.modules.Response
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.launch
import java.io.File

/**
 * The Activity ViewModel ...
 *
 * @see ActivityState
 *
 * @author enoabasi
 * */
class ActivityViewModel : ViewModel() {

    /**
     * A state in this context is an **Activity State** consisting of;
     *  - UserID
     *  - ActivityId
     *  - Images
     *  - Title
     *  - Description
     *  - Location
     *  - Date
     *  - Timestamp
     *
     *  These values can be modified by the view-model and will be consumed by the UI. The state
     *  fetches it values from the Activity State whom by Google principle are all null checked. So
     *  by using the state all values have to be null-wrapped(!!).
     *
     *  @see ActivityState
     *
     *  @author enoabasi
     * */
    var state by mutableStateOf(ActivityState())
        private set

    /**
     * A response is a signal or commute of data from a given source of origin. In this case it
     * represents an activity post state. The state will later be modified and consumed by the
     * UI as well as changes being affected in the console (Firebase Real-time database).
     *
     * @see ActivityState
     *
     * @author enoabasi
     * */
    val response: MutableState<ActivityDataState> = mutableStateOf(ActivityDataState.Empty)

    /**
     * Adds images to the firebase storage folder of images/All. The variable acts as a state holder
     * for all the newly and updated added files
     *
     * @author enoabasi
     * */
    var addImageToStorageResponse by mutableStateOf<Response<Uri?>>(Response.Success(null))
        private set

    /**
     * A database variable referring to the firebase database reference path in recallify's firebase
     * console. The referenced path is targeting the **"users"** path as the sub-root of the
     * database.
     *
     * @see FirebaseDatabase
     *
     * @author enoabasi
     * */
    private val database = FirebaseDatabase.getInstance().reference
        .child("users")

    /**
     * A storage variable referring to the firebase storage reference path in the recallify's
     * firebase console. The reference path is **"imageFolder"**.
     *
     * @see FirebaseStorage
     *
     * @author enoabasi
     *
     * */
    private val storage = FirebaseStorage.getInstance().reference
        .child("imageFolder")

    /**
     * The current user's userID. The value is already being null-checked and safe wrapped. The
     * variable can be called as it is.
     *
     * userID -> userID.
     *
     * @see FirebaseAuth
     *
     * @author enoabasi
     * */
    private val userID = FirebaseAuth.getInstance().currentUser?.uid!!

    /**
     * Init launch that happens before the view-model is called into the application. Handles
     * preparation for retrieving data from the database. The functions called are;
     *
     * 1. fetchDataFromFirebase()
     *
     * @author enoabasi
     * */
//    init {
//        fetchDataFromFirebase()
//    }

    /**
     * Consumes the state change from the UI and updates the changes in the state
     * parent [ActivityState]. The **"title"** of the state will be updated or created depending
     * on the activity.
     *
     * @param title The title of the activity
     *
     * @author enoabasi
     * */
    fun onTitleChange(title: String) {
        state = state.copy(title = title)
    }

    /**
     * Consumes the state change from the UI and updates the changes in the state
     * parent [ActivityState]. The **"description"** of the state will be updated or created
     * depending on the activity.
     *
     * @param description The description of the activity
     *
     * @author enoabasi
     * */
    fun onDescriptionChange(description: String) {
        state = state.copy(description = description)
    }

    /**
     * Consumes the state change from the UI and updates the changes in the state
     * parent [ActivityState]. The **"image"** of the state will be updated or created
     * depending on the activity.
     *
     * @param images The list of images of the user in the activity
     *
     * @author enoabasi
     * */
    private fun onImageChange(images: List<Uri?>) {
        state = state.copy(images = images)
    }

    /**
     * Consumes the state change from the UI and updates the changes in the state
     * parent [ActivityState]. The **"location"** of the state will be updated or created
     * depending on the activity.
     *
     * @param location The location of the current activity
     *
     * @author enoabasi
     * */
    private fun onLocationChange(location: String) {
        state = state.copy(location = location)
    }

    /**
     * Fetches an activity from the real-time database to be consumed by the
     * User Interface, UI. The function produces a class object of all the
     * properties of the **Activity State**.
     *
     * @author enoabasi
     * */
    private fun fetchDataFromFirebase() {
        /**
         * A holding variable to store all the values of an Activity that is being fetched from the
         * database.
         *
         * @author enoabasi
         * */
        val tempList = mutableListOf<ActivityState>()

        response.value = ActivityDataState.Loading

        database
            .child(userID)
            .child(getCurrentDate())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (DataSnap in snapshot.children) {
                        val activityItem = DataSnap.getValue(ActivityState::class.java)
                        tempList.add(activityItem!!)
                    }
                    response.value = ActivityDataState.Success(tempList)
                }

                override fun onCancelled(error: DatabaseError) {
                    response.value = ActivityDataState.Failed(error.message)
                }
            })
    }

    /**
     * Gets images from the device directory and stores it in a collection that can be downloaded
     * into the Firebase storage and referenced in the real-time database. The function updates
     * an already existing collection of images if any are present and if not adds to the collection.
     *
     * The functions requires the permission to access camera dn file directory else it would not
     * work. Each image in the directory is given a unique has key to prevent duplication and also
     * scenarios where the user adds an image pauses the app, resumes the app and adds another image
     * of the same type.
     *
     * @param listOfImages This is a collection of images taken from the directory. It will be to
     *                       the firebase storage bucket in the console.
     *
     * @author enoabasi
     * */
    fun updateSelectedImageList(listOfImages: List<Uri>) {
        val updatedImageList = emptyList<Uri>().toMutableList()
        viewModelScope.launch {
            updatedImageList += listOfImages
            val imagesUploads = addToFirebaseStorage(updatedImageList)
            onImageChange(images = imagesUploads)
        }
    }

    private fun addToFirebaseStorage(list : MutableList<Uri>) : MutableList<Uri> {
            val imageFolder = storage
            val downloads = emptyList<Uri>().toMutableList()
            for (uri in list) {
                val imageName = imageFolder.child(userID).child(getCurrentDate()).putFile(uri).continueWithTask {
                    task ->
                    if (!task.isSuccessful) {
                        task.exception?.let {
                            throw it
                        }
                    }
                    imageFolder.downloadUrl
                }.addOnSuccessListener {task ->
                    downloads.add(task)
                }
            }
            return downloads
    }

    /**
     * Removes an image item from the collection list at any index if selected.
     *
     * @param index The image to be removed from the collection
     *
     * @author enoabasi
     * */
    fun onItemRemove(index: Int) {
        val updatedImageList = state.images.toMutableList()
        viewModelScope.launch {
            updatedImageList.removeAt(index)

        }
    }

    /**
     * Creates a user's activity.
     *
     * @param scaffoldState The state of the scaffold layout
     *
     * @return A job of the complete post/Activity sequence. The post/Activity sequence is sending
     * the Activity State data to the firebase real-time database.
     *
     * @since 1.0.0
     *
     * @author enoabasi
     * */
    fun createActivity(
        scaffoldState: ScaffoldState,
        imageUri : List<Uri?>
    ) = viewModelScope.launch {
        try {
            if (!validateActivityPost()) {
                scaffoldState.snackbarHostState.showSnackbar(
                    message = "Title cannot be empty!",
                    duration = SnackbarDuration.Short
                )
            }

            val dataRef = FirebaseDatabase.getInstance().reference

            val activityDatabase = database
                .child(userID)
                .child("dailyDairyDummy")
                .child(getCurrentDate())

            activityDatabase.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var activityID = snapshot.childrenCount.toInt()
                    activityID += 1

                    // UserID
                    dataRef.child("users")
                        .child(userID)
                        .child("dailyDairyDummy")
                        .child(getCurrentDate())
                        .child(activityID.toString())
                        .child("userId")
                        .setValue(userID)
                    // Activity ID
                    dataRef.child("users")
                        .child(userID)
                        .child("dailyDairyDummy")
                        .child(getCurrentDate())
                        .child(activityID.toString())
                        .child("activityId")
                        .setValue(activityID.toString())
                    // ImageLink
                    dataRef.child("users")
                        .child(userID)
                        .child("dailyDairyDummy")
                        .child(getCurrentDate())
                        .child(activityID.toString())
                        .child("imageLink")
                        .setValue("Xsxbi3qj2XbYUdNDUo7yg7JAuOd2, description=zbzgsusbsbsvshsus, location=, title=dgsjsgsvsvsjsu}, 4={activityId=4, imageLink=https://firebasestorage.googleapis.com/v0/b/csci-recallify.appspot.com/o/imageFolder%2FXsxbi3qj2XbYUdNDUo7yg7JAuOd2%2F2023-03-23%2Fimage%3A41538?alt=media&token=774f3718-7b70-4d5e-ba91-2dcd1589d74a,")
                    // Title
                    dataRef.child("users")
                        .child(userID)
                        .child("dailyDairyDummy")
                        .child(getCurrentDate())
                        .child(activityID.toString())
                        .child("title")
                        .setValue(state.title)
                    // Description
                    dataRef.child("users")
                        .child(userID)
                        .child("dailyDairyDummy")
                        .child(getCurrentDate())
                        .child(activityID.toString())
                        .child("description")
                        .setValue(state.description)
                    // Location
                    dataRef.child("users")
                        .child(userID)
                        .child("dailyDairyDummy")
                        .child(getCurrentDate())
                        .child(activityID.toString())
                        .child("location")
                        .setValue(state.location)
                    // Date
                    dataRef.child("users")
                        .child(userID)
                        .child("dailyDairyDummy")
                        .child(getCurrentDate())
                        .child(activityID.toString())
                        .child("date-created")
                        .setValue(getCurrentDate())
                    // Timestamp
                    dataRef.child("users")
                        .child(userID)
                        .child("dailyDairyDummy")
                        .child(getCurrentDate())
                        .child(activityID.toString())
                        .child("timestamp")
                        .setValue(state.timestamp)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Errors(RealTimeDatabase)","error on ${error.message}")
                }

            }).let {

            }
        } catch (e: Exception) {
            Log.e("Error: ", "Failed to send message at: ${e.message}")
        }
    }

    /**
     * Asserts if the title of an activity is not blank or empty. The title serves as a caption to
     * identify an activity. This is so as a description may not be needed to understand the
     * context of an activity.
     *
     * @return The Boolean validation of the title, True | False.
     *
     * @author enoabasi
     * */
    private fun validateActivityPost() = state.title!!.isNotBlank()

}