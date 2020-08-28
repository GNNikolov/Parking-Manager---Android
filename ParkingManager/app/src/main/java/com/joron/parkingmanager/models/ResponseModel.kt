package com.joron.parkingmanager.models

/**
 * Created by Joro on 27/08/2020
 */
sealed class ResponseModel {
    object Loading : ResponseModel()

    open class Success<T> private constructor() : ResponseModel() {
        constructor(data: List<T>) : this()
        constructor(data: T) : this()
    }

    data class Error(val code: Int) : ResponseModel()
}