package com.joron.parkingmanager.viewmodel

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.joron.parkingmanager.models.BleState
import com.joron.parkingmanager.ui.BleStatusLayout

/**
 * Created by Joro on 16/03/2020
 */
class BleStateViewModel : ViewModel() {
    private var locationGranted = false
    val bleLiveData = MutableLiveData<BleState>()
    private val hasFineLocation = MutableLiveData<Boolean>()
    fun locationEnableLiveData() :LiveData<Boolean> = hasFineLocation

    companion object{
        @BindingAdapter("android:background")
        @JvmStatic
        fun setBleView(statusLayout: BleStatusLayout, state: BleState?){
            when(state){
                BleState.BleConnecting -> {
                    statusLayout.setConnecting()
                    return
                }
                BleState.NoLocation -> {
                    statusLayout.needsLocation()
                    return
                }
                BleState.LocationEnabled ->{
                    statusLayout.setEnableBluetooth()
                    return
                }
            }
            if (state is BleState.ServiceFound){
                statusLayout.setServiceFound(state)
                return
            }
            if (state is BleState.BleConnected) {
                statusLayout.setConnected(state.getDeviceName())
            }
        }
    }

    fun setGPSToggleValue(enabled: Boolean){
        if (!locationGranted){
            hasFineLocation.value = enabled
        }
        if (enabled){
            locationGranted = true
        }
    }

}