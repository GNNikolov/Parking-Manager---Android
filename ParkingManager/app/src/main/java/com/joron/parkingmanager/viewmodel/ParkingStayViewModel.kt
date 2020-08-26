package com.joron.parkingmanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.liveData
import com.joron.parkingmanager.models.ParkingStay
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
        val data: Response<List<ParkingStay>>? = try {
            //if(jwt != null)
                apiClient.getAllParkingStays("Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwaG9uZSI6IiszNTk4ODY1NTYwODUiLCJmY20iOiJmbi13Z3NfUVQ5QzI1S18tT2NkN01HOkFQQTkxYkdzbGNOVkV0OFI4aEZlS0t0NkNkS0tERXJ1a00xZXNMTGRocmZqVGZVSGFQQnJHLVZFYnBLT0NhUUY3c2hPR294a1F6Q003dUZfMWZoelp5LVZDWl8tb0N5YUtfRzEzNW9qOGp6TjdqV2lRQXgxWmlWN254UHBYLVV3VjgwcTd0QzFHLVh3IiwiaXNzIjoicGFya2luZ0FwcC5jb20ifQ.Fd-dATkYORES8tGIafOM8F3ppPGafvXXhoH5F0wzJFY")
            //else
              //  null
        } catch (e: Exception) {
            null
        }
        emit(data)
    }
}