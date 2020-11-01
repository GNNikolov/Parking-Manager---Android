package com.joron.parkingmanager.models

data class CarResponseModel(val data: List<Car>) : ResponseModel.Success() {
}