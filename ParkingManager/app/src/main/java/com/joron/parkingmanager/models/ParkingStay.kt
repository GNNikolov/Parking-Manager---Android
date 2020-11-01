package com.joron.parkingmanager.models

/**
 * Created by Joro on 24/08/2020
 */
data class ParkingStay(val dateTimeEntered: String, val dateTimeExited: String? = null, val plateNo: String)