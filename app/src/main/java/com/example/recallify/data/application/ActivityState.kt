package com.example.recallify.data.application

import android.net.Uri
import com.google.firebase.database.ServerValue
import java.text.DateFormat
import java.util.*

data class ActivityState(
    val userId: String? = "",
    val activityId: String? = "",
    val images: List<Uri>? = emptyList(),
    var title: String? = "",
    var description: String? = "",
    val date: String? = DateFormat.getDateInstance(DateFormat.SHORT).format(Calendar.getInstance().time),
    val timestamp: MutableMap<String, String> = ServerValue.TIMESTAMP,
) {
    fun mapActivity(): Map<String, Any?> {
        return mapOf(
            "user-id" to userId,
            "activityId" to activityId,
            "images" to images,
            "title" to title,
            "description" to description,
            "date" to date,
            "time-stamp" to timestamp
        )
    }
}