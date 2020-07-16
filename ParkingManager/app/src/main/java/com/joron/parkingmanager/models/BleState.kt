package com.joron.parkingmanager.models

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattService

/**
 * Created by Joro on 16/03/2020
 */
sealed class BleState {
    object NoLocation : BleState()
    object BleNotConnected : BleState()
    object BleConnecting : BleState()
    open class BleConnected(private val gattDevice: BluetoothDevice) : BleState(){
        fun getDeviceName(): String = gattDevice.name
        fun getDeviceAddress(): String = gattDevice.address
    }
    data class ServiceFound(private val gattDevice: BluetoothDevice, val service: BluetoothGattService) : BleConnected(gattDevice)
    object LocationEnabled: BleState()
}