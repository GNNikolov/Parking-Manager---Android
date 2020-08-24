package com.joron.parkingmanager.util

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Joro on 24/08/2020
 */
object DateTimeUtils {

    //return now date in format: "yyyy-MM-dd'T'HH:mm:ss
    fun currentDateFormatted(): String {
        val pattern = "yyyy-MM-dd'T'HH:mm:ss"
        val sdf = SimpleDateFormat( pattern, Locale.getDefault())
        return sdf.format(System.currentTimeMillis())
    }

}