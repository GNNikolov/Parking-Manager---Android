package com.joron.parkingmanager.models

/**
 * Created by Joro on 28/08/2020
 */
data class ParkingStayResponseModel(val data: List<ParkingStay>) : ResponseModel.Success<ParkingStay>(data)