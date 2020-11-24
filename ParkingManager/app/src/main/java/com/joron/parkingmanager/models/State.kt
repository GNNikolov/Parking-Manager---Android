package com.joron.parkingmanager.models

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattService

/**
 * Created by Joro on 16/03/2020
 */
sealed class State {
    object NoLocation : State()
    object BleConnecting : State()
    object NotDeviceFound : State()
    open class BleConnected(private val gattDevice: BluetoothDevice) : State(){
        fun getDeviceName(): String = gattDevice.name
    }
    data class ServiceFound(private val gattDevice: BluetoothDevice, val service: BluetoothGattService) : BleConnected(gattDevice)
    object CharacteristicWritten : State()
    object LocationEnabled: State()
}