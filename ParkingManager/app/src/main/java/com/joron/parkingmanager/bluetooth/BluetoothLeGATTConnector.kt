package com.joron.parkingmanager.bluetooth

import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.joron.parkingmanager.models.BleState
import com.joron.parkingmanager.viewmodel.BleStateViewModel
import java.util.*

/**
 * Created by Joro on 23/08/2020
 */
internal class BluetoothLeGATTConnector(
    private val scanner: BluetoothLeScanner,
    private val viewModel: BleStateViewModel
) : BluetoothGattCallback() {
    private var bleTXCharacteristic: BluetoothGattCharacteristic? = null
    private var bleRXCharacteristic: BluetoothGattCharacteristic? = null
    private var mGatt: BluetoothGatt? = null

    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            if (msg.what == BLE_FIND_SERVICES) {
                mGatt?.discoverServices()
            }
        }
    }

    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
        if (newState == BluetoothGatt.STATE_CONNECTED) {
            mGatt = gatt
            val message = Message().apply {
                what = BLE_FIND_SERVICES
            }
            mHandler.sendMessageDelayed(message, CONNECT_DELAY_MS)
            scanner.scanLeDevice(false)// stop scanning...
        } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
            scanner.scanLeDevice(true)
        }
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
        val characteristic = gatt?.getService(GENERAL_UUID)
        bleTXCharacteristic = characteristic?.getCharacteristic(TX_UUID)
        bleRXCharacteristic = characteristic?.getCharacteristic(RX_UUID)
        if (!isUIThread()) {
            viewModel.bleLiveData.postValue(BleState.ServiceFound(gatt?.device!!, characteristic!!))
        } else {
            viewModel.bleLiveData.value = BleState.ServiceFound(gatt?.device!!, characteristic!!)
        }
    }

    fun rotate() = bleTXCharacteristic?.let {
        mGatt?.setCharacteristicNotification(it, true)
        bleTXCharacteristic?.value = ROTATION_START_COMMAND.toByteArray()
        mGatt?.writeCharacteristic(bleTXCharacteristic)
    }

    fun close() {
        mGatt?.disconnect()
        mGatt?.close()
        mHandler.removeMessages(BLE_FIND_SERVICES)
    }

    private fun isUIThread(): Boolean = Looper.myLooper() == Looper.getMainLooper()

    companion object {
        private const val BLE_FIND_SERVICES = 0
        private val TX_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e")

        private val RX_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e")

        private val GENERAL_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")

        private const val ROTATION_START_COMMAND = "rotr"

        private const val CONNECT_DELAY_MS: Long = 700
    }
}