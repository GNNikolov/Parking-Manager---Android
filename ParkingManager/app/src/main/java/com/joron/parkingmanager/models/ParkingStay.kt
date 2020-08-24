package com.joron.parkingmanager.models

import com.joron.parkingmanager.util.DateTimeUtils

/**
 * Created by Joro on 24/08/2020
 */
data class ParkingStay(val eventReported: EventReported, val plateNo: String) {
    val dateTimeReported: String = DateTimeUtils.currentDateFormatted()
}