package com.example.recallify.view.ui.feature.application.tbi_applications.dailydiary.daily_activity

import android.net.Uri
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
    var addImageToStorageResponse by mutableStateOf<Response<Uri>?>(Response.Success(null))
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
     * An incrementing ID key for daily dairy activities. The keys are generated every time a new
     * post is being made. This allows for the side quest to be able to track the activities
     * every time they are being made.
     *
     * The keys restart after a new day has been entered.
     *
     * @returns A unique increment key
     * @author enoabasi ridinbal
     * */
    private var activityKeyIncrementer = 1

    /**
     * Init launch that happens before the view-model is called into the application. Handles
     * preparation for retrieving data from the database. The functions called are;
     *
     * 1. fetchDataFromFirebase()
     *
     * @author enoabasi
     * */
    init {
        fetchDataFromFirebase()
    }

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
     * Adds the image from the user's phone to the firebase storage.
     *
     * @param imageUri The containing list of image URIs from the user's phone directory
     *
     * @author
     * */
    fun addImageToFirebaseStorage(imageUri: List<Uri?>) {
        try {
            Response.Loading

            /**
             * The storage bucket location reference.
             *
             * @author enoabasi
             * */
            val imageFolder = storage
            val downloadUris : MutableList<Uri?> = emptyList<Uri?>().toMutableList()

            for (uri in imageUri.indices) {

                /**
                 * This is the value count of a single image gotten from the user.
                 *
                 * @author enoabasi
                 * */
                val singleImage = imageUri[uri]

                /**
                 * The name of the image reference on the firebase storage bucket.
                 *
                 * @author enoabasi
                 * */
                val imageName = imageFolder
                    .child("${singleImage?.lastPathSegment}")

                imageName.putFile(singleImage!!).addOnSuccessListener {
                    imageName.downloadUrl.addOnSuccessListener {
                        Response.Success(it)
                        downloadUris.add(it)
                    }
                }
                addImageToFirebaseDatabase(downloadUris)
            }
        } catch (message: Exception) {
            Response.Failure(message)
        }
    }

    /**
     * Downloads the image from teh firebase Storage and saves it into the firebase Real-time
     * database
     *
     * @param downloadUrl The downloaded image url from the Firebase storage
     *
     * @author enoabasi
     * */
    private fun addImageToFirebaseDatabase(downloadUrl: List<Uri?>) {
        try {
            Response.Loading

            for (url in downloadUrl) {
                database
                    .updateChildren(
                        hashMapOf<String, Any>(
                            "/$userID/${getCurrentDate()}/${activityKeyIncrementer}" to url!!
                        )
                    )
            }
            addImageLinkToFirebase(downloadUrl = downloadUrl[0])
        } catch (message: Exception) {
            Response.Failure(message)
        }
    }


    /**
     *
     * Adds a single reference image to the a unique path in the database. This wil be used for the
     * side quest feature. The image is gotten from a list of images and the return value is a
     * single of the first image in the list.
     *
     * The image list os provided by another higher-function [addImageToFirebaseDatabase].
     *
     * @author  enoabasi, ridinbal
     *
     * @see FirebaseStorage
     * @see FirebaseDatabase
     * */
    private fun addImageLinkToFirebase(downloadUrl: Uri?) {
        try {
            Response.Loading
            database
                .updateChildren(
                    hashMapOf<String, Any>(
                        "/$userID/dailyDairyDummy/${getCurrentDate()}/${activityKeyIncrementer}/imageLink" to downloadUrl!!
                    )
                )
        } catch (message: Exception) {
            Response.Failure(message)
        }
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
        val updatedImageList = state.images.toMutableList()
        viewModelScope.launch {
            updatedImageList += listOfImages
            onImageChange(updatedImageList.distinct())
        }
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
            onImageChange(images = updatedImageList.distinct())
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
    ) = viewModelScope.launch {
        try {
            if (!validateActivityPost()) {
                scaffoldState.snackbarHostState.showSnackbar(
                    message = "Title cannot be empty!",
                    duration = SnackbarDuration.Short
                )
            }

            val activityState = ActivityState(
                userId = userID,
                activityId = activityKeyIncrementer.toString(),
                title = state.title,
                description = state.description,
                location = state.location,
            )

            val activityStateValues = activityState.mapActivity()


            val activityStateUpdates = hashMapOf<String, Any>(
                "/$userID/dailyDairyDummy/${getCurrentDate()}/$activityKeyIncrementer" to activityStateValues,
            )

            database.updateChildren(activityStateUpdates)

            activityKeyIncrementer++
        } catch (e: Exception) {
            response.value = e.message?.let { ActivityDataState.Failed(it) }!!
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