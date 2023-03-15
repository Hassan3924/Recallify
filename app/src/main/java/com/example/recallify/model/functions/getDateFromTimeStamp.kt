package com.example.recallify.model.functions

import java.text.SimpleDateFormat
import java.util.*

fun getDateFromTimeStamp(timestamp: Long) : String {
    return try{
        val parseDate = Date(timestamp)
        val simpleDate = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        simpleDate.format(parseDate)
    } catch (e: Exception) {
        "date"
    }
}

fun getTimeFromTimeSTamp(timestamp: Long) : String {
    return try{
        val parseDate = Date(timestamp)
        val simpleDate = SimpleDateFormat("h:mm a", Locale.getDefault())
        simpleDate.format(parseDate)
    } catch (e: Exception) {
        "time"
    }
}