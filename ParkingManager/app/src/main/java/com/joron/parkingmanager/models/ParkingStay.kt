package com.joron.parkingmanager.models

/**
 * Created by Joro on 24/08/2020
 */
data class ParkingStay(
    val dateTimeReported: String,
    val event: Int,
    val plateNo: String
)