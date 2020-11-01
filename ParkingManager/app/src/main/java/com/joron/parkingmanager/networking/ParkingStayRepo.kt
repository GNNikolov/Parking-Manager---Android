package com.joron.parkingmanager.networking

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.joron.parkingmanager.models.ParkingStay
import com.joron.parkingmanager.models.ParkingStayResponseModel
import com.joron.parkingmanager.models.ResponseModel
import kotlinx.coroutines.Dispatchers
import retrofit2.Response

class ParkingStayRepo(context: Context) : AbstractRepo<ParkingStay>(context) {

    override fun get(): LiveData<ResponseModel> = liveData(Dispatchers.IO) {
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

    override fun post(data: ParkingStay) = liveData(Dispatchers.IO) {
        var response: Response<Int>? = null
        try {
            if (jwt != null) {
                emit(ResponseModel.Loading)
                response = apiClient.insertParkingStay(data, ApiClient.passJWT(jwt!!))
                if (response.isSuccessful && response.code() == 200) {
                    when (response.message().toInt()) {
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

    override fun update(data: ParkingStay) = liveData(Dispatchers.IO) {
        var response: Response<Int>? = null
        try {
            if (jwt != null) {
                emit(ResponseModel.Loading)
                response = apiClient.updateParkingStay(data, ApiClient.passJWT(jwt!!))
                if (response.isSuccessful && response.code() == 200) {
                    when (response.message().toInt()) {
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