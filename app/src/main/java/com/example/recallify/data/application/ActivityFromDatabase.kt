package com.example.recallify.data.application

import android.net.Uri

data class ActivityFromDatabase(
    var userId: String? ="",
    var activityId: String? = "",
    var images: List<Uri?> = emptyList(),
    var imageLink: String? = "",
    var title: String? = "",
    var description: String? = "",
    var location: String? = "",
    var date: String? = "",
    var timestamp: String? = "",
)
