package com.joron.parkingmanager.models

/**
 * Created by Joro on 27/08/2020
 */
sealed class Response {
    object Loading : Response()
    data class Success(val data: List<ParkingStay>) : Response()
    data class Error(val code: Int) : Response()
}