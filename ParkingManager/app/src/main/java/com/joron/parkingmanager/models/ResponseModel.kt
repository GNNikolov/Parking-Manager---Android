package com.joron.parkingmanager.models

/**
 * Created by Joro on 27/08/2020
 */
sealed class ResponseModel {
    object Loading : ResponseModel()

    open class Success : ResponseModel()

    data class Error(val code: Int) : ResponseModel()
}