package com.joron.parkingmanager.viewmodel

import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.joron.parkingmanager.models.State
import com.joron.parkingmanager.ui.BluetoothStateView

/**
 * Created by Joro on 16/03/2020
 */
class BluetoothLocationViewModel : ViewModel() {
    private var locationGranted = false
    val stateLiveData = MutableLiveData<State>()
    private val _locationLiveData = MutableLiveData<Boolean>()
    val locationLiveData : LiveData<Boolean>
        get() = _locationLiveData

    companion object {
        @BindingAdapter("android:background")
        @JvmStatic
        fun setBleView(statusLayout: BluetoothStateView, state: State?){
            when(state){
                State.BleConnecting -> {
                    statusLayout.setConnecting()
                    return
                }
                State.NoLocation -> {
                    statusLayout.needsLocation()
                    return
                }
                State.LocationEnabled ->{
                    statusLayout.setEnableBluetooth()
                    return
                }
            }
            if (state is State.ServiceFound){
                statusLayout.setServiceFound(state)
                return
            }
            if (state is State.BleConnected) {
                statusLayout.setConnected(state.getDeviceName())
            }
        }
    }

    fun setGPSToggleValue(enabled: Boolean){
        if (!locationGranted){
            _locationLiveData.value = enabled
        }
        if (enabled){
            locationGranted = true
        }
    }

}