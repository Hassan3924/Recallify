package com.example.recallify.model.functions

import java.text.SimpleDateFormat
import java.util.*

/**
 * This function breaks a Firebase SERVER.timestamp value to retrieve the date. The timestamp is
 * Firebase database timer that keeps track of the date and time a collection or piece of data was
 * added to the database.
 *
 * @param timestamp The timestamp retrieved from the database.
 *
 * @return The string form of the timestamp date in format **YYYY.MM.DD, year.month.date**
 *
 * @author enoabasi
 * */
fun getDateFromTimeStamp(timestamp: MutableMap<String, String>) : String {

    return try{
        val key = timestamp.keys.toString()
        val longKey = key.toLong()
        val parseDate = Date(longKey)
        val simpleDate = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        simpleDate.format(parseDate)
    } catch (e: Exception) {
        "date"
    }
}
/**
 * This function breaks a Firebase SERVER.timestamp value to retrieve the time. The timestamp is
 * Firebase database timer that keeps track of the date and time a collection or piece of data was
 * added to the database.
 *
 * @param timestamp The timestamp retrieved from the database.
 *
 * @return The string form of the timestamp time in format **H.MM.A, hour.minutes.ampere**. Ampere is
 * referred to as the greenwich plane. it can be **AM** or **PM**.
 *
 * @author enoabasi
 * */
fun getTimeFromTimeSTamp(timestamp: MutableMap<String, String>) : String {
    return try{
        val key = timestamp.keys.toString()
        val longKey = key.toLong()
        val parseDate = Date(longKey)
        val simpleDate = SimpleDateFormat("h:mm a", Locale.getDefault())
        simpleDate.format(parseDate)
    } catch (e: Exception) {
        "time"
    }
}