package com.joron.parkingmanager.networking

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.joron.parkingmanager.models.Car
import com.joron.parkingmanager.models.CarResponseModel
import com.joron.parkingmanager.models.ParkingStayResponseModel
import com.joron.parkingmanager.models.ResponseModel
import kotlinx.coroutines.Dispatchers
import retrofit2.Response

class CarRepo(context: Context) : AbstractRepo<Car>(context) {

    override fun get(): LiveData<ResponseModel> = liveData(Dispatchers.IO) {
        var response: Response<List<Car>>? = null
        try {
            if (jwt != null) {
                emit(ResponseModel.Loading)
                response = apiClient.getAllCars(ApiClient.passJWT(jwt!!))
                response.body()?.let {
                    emit(CarResponseModel(it))
                }
            } else
                emit(ResponseModel.Error(401))
        } catch (e: Exception) {
            emit(ResponseModel.Error(response?.code() ?: -1))
        }
    }

    override fun post(data: Car): LiveData<ResponseModel> = liveData(Dispatchers.IO) {
        var response: Response<Int>? = null
        try {
            if (jwt != null) {
                emit(ResponseModel.Loading)
                response = apiClient.insertCar(data, ApiClient.passJWT(jwt!!))
                if (response.isSuccessful && response.code() == 200) {
                    when (response.body()) {
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

}