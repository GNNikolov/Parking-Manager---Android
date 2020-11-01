package com.joron.parkingmanager.networking

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.joron.parkingmanager.models.ResponseModel
import com.joron.parkingmanager.util.Util

abstract class AbstractRepo<T>(protected val context: Context) {
    protected val apiClient = NetworkService.apiClient
    protected val jwt = Util.getJWTToken(context.applicationContext)

    abstract fun get(): LiveData<ResponseModel>

    abstract fun post(data: T): LiveData<ResponseModel>

    open fun update(data: T): LiveData<ResponseModel> = liveData { }

}