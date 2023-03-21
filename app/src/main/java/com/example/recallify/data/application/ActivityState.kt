package com.example.recallify.data.application

import android.net.Uri
import com.google.firebase.database.ServerValue.TIMESTAMP
import java.text.DateFormat
import java.util.*

/**
 * The ActivityState defines an activity for the feature "Daily Diary". This feature allows our
 * user's to create a post-like activity of their event. It is stored in the database and fed back
 * to them about what happened during that event. It can be sorted by date.
 *
 * @param userId The current user's user ID, used for identifying the user
 * @param activityId The activity's activity ID, used for identifying the activity
 * @param images The images provided by the user to the used as in the activity
 * @param title The title of an activity
 * @param description The description of an activity
 * @param location The location of the user at that the time the activity was made
 * @param date The date of an activity, it is auto-generated by the user's machine
 * @param timestamp The timestamp of the activity, it is auto-generated and used by firebase for
 * sorting and indexing
 *
 * @author enoabasi
 * */
data class ActivityState(
    val userId: String? = "",
    val activityId: String? = "",
    val images: List<Uri>? = emptyList(),
    var title: String? = "",
    var description: String? = "",
    val location: String? = "",
    val date: String? = DateFormat.getDateInstance(DateFormat.SHORT).format(Calendar.getInstance().time),
    val timestamp: MutableMap<String, String> = TIMESTAMP,
) {

    /**
     * This maps the values of the Activity State to a provided map in any given source. An example
     * is the firebase ValueEventListener which can take a map of a given state if the state has a
     * map function. This makes it easier for updates to take place and reduces the time for
     * development.
     *
     * @return A map of the ActivityStates for changes or updates. The changes could be Create,
     * Delete. While, Update is source update from local or a network. The return of the map is
     * typed **<String, Any>**.
     *
     * @author enoabasi
     * */
    fun mapActivity(): Map<String, Any?> {
        return mapOf(
            "user-id" to userId,
            "activityId" to activityId,
            "images" to images,
            "title" to title,
            "description" to description,
            "location" to location,
            "date" to date,
            "time-stamp" to timestamp
        )
    }
}