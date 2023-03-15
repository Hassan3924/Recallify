package com.example.recallify.model.repo

import android.content.ContentValues.TAG
import android.util.Log
import com.example.recallify.data.application.Activity
import com.example.recallify.view.ui.resource.modules.Resource
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

const val NOTES_COLLECTION_REF = "activities"

class RealTimeRepository {
    val user = Firebase.auth.currentUser
    fun hasUser(): Boolean = Firebase.auth.currentUser != null

    private fun getUserID(): String = Firebase.auth.currentUser?.uid.orEmpty()

    private val database = Firebase.database.reference
    private val rootReference = database.child("users").child(getUserID())
    private val activitiesReference = rootReference.child(NOTES_COLLECTION_REF)
    private val activityKey = activitiesReference.key

    private val activityRef = activitiesReference.child(activityKey!!)

    fun getUserActivities(
        userId: String,
    ): Flow<Resource<List<Activity>>> = callbackFlow {
        try {
            val activityList = mutableListOf<Activity>()
            val activityListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (activitySnapshot in snapshot.children) {
                        val activity = activitySnapshot.getValue(Activity::class.java)
                        activity?.let { activityList.add(it) }
                    }
                    Resource.Success(data = activityList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w(TAG, "There seems to be a problem with the database.")
                }
            }

            activityRef.orderByChild("timestamp")
                .addListenerForSingleValueEvent(activityListener)

            awaitClose {
                activityRef.removeEventListener(activityListener)
            }
        } catch (e: Exception) {
            Log.w(TAG, "There seems to be a problem with the database.")
        }

    }
}