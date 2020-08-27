package com.joron.parkingmanager.viewmodel

import android.app.Application
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.joron.parkingmanager.R
import com.joron.parkingmanager.models.EventReported
import com.joron.parkingmanager.models.ParkingStay
import com.joron.parkingmanager.networking.ApiClient
import com.joron.parkingmanager.networking.NetworkService
import com.joron.parkingmanager.util.Util
import kotlinx.coroutines.Dispatchers
import retrofit2.Response

/**
 * Created by Joro on 26/08/2020
 */
class ParkingStayViewModel(application: Application) : AndroidViewModel(application) {
    private val apiClient by lazy {
        NetworkService.apiClient
    }
    private val jwt by lazy {
        Util.getJWTToken(application)
    }

    fun fetchParkingStays() = liveData(Dispatchers.IO) {
        var data: Response<List<ParkingStay>>? = null
        try {
            if (jwt != null) {
                emit(com.joron.parkingmanager.models.Response.Loading)
                data = apiClient.getAllParkingStays(ApiClient.passJWT(jwt!!))
                data.body()?.let {
                    emit(com.joron.parkingmanager.models.Response.Success(it))
                }
            } else {
                emit(com.joron.parkingmanager.models.Response.Error(401))
            }
        } catch (e: Exception) {
            emit(com.joron.parkingmanager.models.Response.Error(data?.code() ?: -1))
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter("android:text")
        fun setView(view: TextView, data: ParkingStay) {
            val context = view.context
            val message = if (data.eventReported == EventReported.CHECK_IN)
                context.getString(R.string.enter_on, data.dateTimeReported)
            else
                context.getString(R.string.exit_on, data.dateTimeReported)
            view.text = message
        }
    }
}