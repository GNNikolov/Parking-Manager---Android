package com.joron.parkingmanager.viewmodel

import android.app.Application
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.joron.parkingmanager.R
import com.joron.parkingmanager.models.ParkingStay
import com.joron.parkingmanager.models.ParkingStayResponseModel
import com.joron.parkingmanager.models.ResponseModel
import com.joron.parkingmanager.networking.ApiClient
import com.joron.parkingmanager.networking.NetworkService
import com.joron.parkingmanager.util.Util
import kotlinx.coroutines.Dispatchers
import retrofit2.Response
import java.util.concurrent.TimeUnit

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
                emit(ResponseModel.Loading)
                data = apiClient.getAllParkingStays(ApiClient.passJWT(jwt!!))
                data.body()?.let {
                    emit(ParkingStayResponseModel(it))
                }
            } else {
                emit(ResponseModel.Error(401))
            }
        } catch (e: Exception) {
            emit(ResponseModel.Error(data?.code() ?: -1))
        }
    }

    fun enterParking(parkingStay: ParkingStay) = liveData(Dispatchers.IO) {
        var response: Response<Int>? = null
        try {
            if (jwt != null) {
                emit(ResponseModel.Loading)
                response = apiClient.insertParkingStay(parkingStay, ApiClient.passJWT(jwt!!))
                if (response.isSuccessful && response.code() == 200) {
                    when(response.message().toInt()) {
                        0 -> emit(ResponseModel.Error(0))
                        1 -> emit(ResponseModel.Success())
                    }                }
            } else
                 emit(ResponseModel.Error(401))
        } catch (e: Exception) {
            emit(ResponseModel.Error(response?.code() ?: -1))
        }
    }

    fun exitParking(parkingStay: ParkingStay) = liveData(Dispatchers.IO) {
        var response: Response<Int>? = null
        try {
            if (jwt != null) {
                emit(ResponseModel.Loading)
                response = apiClient.updateParkingStay(parkingStay, ApiClient.passJWT(jwt!!))
                if (response.isSuccessful && response.code() == 200) {
                    when(response.message().toInt()) {
                        0 -> emit(ResponseModel.Error(0))
                        1 -> emit(ResponseModel.Success())
                    }
                }
            } else
                emit(ResponseModel.Error(401))
        } catch (e: Exception) {
            emit(ResponseModel.Error(response?.code() ?: -1))
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter("android:text")
        fun setView(view: TextView, data: ParkingStay) {
            val context = view.context
            val dateTimeExitStr = data.dateTimeExited ?: return
            val dateTimeEnterStr = data.dateTimeEntered

            val dateTimeEnter = Util.parseFormattedDate(dateTimeEnterStr) ?: return
            val dateTimeExit = Util.parseFormattedDate(dateTimeExitStr) ?: return

            val timeStayed = dateTimeExit.time - dateTimeEnter.time
            val daysSpent = TimeUnit.DAYS.convert(timeStayed, TimeUnit.MILLISECONDS)
            val minutesSpent = TimeUnit.MINUTES.convert(timeStayed, TimeUnit.MILLISECONDS)
            val hoursSpent = TimeUnit.HOURS.convert(timeStayed, TimeUnit.MILLISECONDS)
            when {
                daysSpent > 0 -> context.getString(R.string.days, daysSpent)
                hoursSpent > 0 -> context.getString(R.string.hours, hoursSpent)
                minutesSpent > 0 -> context.getString(R.string.minutes, minutesSpent)
                else -> null
            }?.also { msg ->
                view.text = context.getString(R.string.parking_spent_time, msg)
            }
        }
    }
}