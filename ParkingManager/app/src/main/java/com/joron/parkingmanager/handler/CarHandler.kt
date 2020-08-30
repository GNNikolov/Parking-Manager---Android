package com.joron.parkingmanager.handler

import com.joron.parkingmanager.models.Car

/**
 * Created by Joro on 30/08/2020
 */
interface CarHandler {
    fun onClicked(car: Car)
    fun onLongCarClicked(car: Car): Boolean
}