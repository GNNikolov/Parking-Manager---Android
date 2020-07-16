package com.joron.parkingmanager.components

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.joron.parkingmanager.models.BleState
import com.joron.parkingmanager.viewmodel.BleStateViewModel
import java.util.*

/**
 * Created by Joro on 09/03/2020
 */
class BluetoothLeScanner (
    private val context: FragmentActivity,
    private val bluetoothAdapter: BluetoothAdapter,
    private val viewModel: BleStateViewModel
) : ScanCallback(), LifecycleObserver {

    private var scanning: Boolean = false
    private val filter = ScanFilter.Builder().setDeviceName(DEVICE_NAME).build()
    private val filters = arrayListOf<ScanFilter>(filter)
    private val settings = ScanSettings.Builder().build()
    private var bleTXCharacteristic: BluetoothGattCharacteristic? = null
    private var bleRXCharacteristic: BluetoothGattCharacteristic? = null
    private var mGatt: BluetoothGatt? = null
    private val mHandler = object : Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            if (msg.what == BLE_FIND_SERVICES){
                mGatt?.discoverServices()
            }
        }
    }
    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothGatt.STATE_CONNECTED ) {
                mGatt = gatt
                val message = Message().apply {
                    what = BLE_FIND_SERVICES
                }
                mHandler.sendMessageDelayed(message, CONNECT_DELAY_MS)
                scanLeDevice(false)// stop scanning...
            } else if(newState == BluetoothGatt.STATE_DISCONNECTED){
                scanLeDevice(true)
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            val characteristic = gatt?.getService(GENERAL_UUID)
            bleTXCharacteristic = characteristic?.getCharacteristic(TX_UUID)
            bleRXCharacteristic = characteristic?.getCharacteristic(RX_UUID)
            if (!isUIThread()){
                viewModel.bleLiveData.postValue(BleState.ServiceFound(gatt?.device!!, characteristic!!))
            } else {
                viewModel.bleLiveData.value = BleState.ServiceFound(gatt?.device!!, characteristic!!)
            }
        }

    }

    init {
        context.lifecycle.addObserver(this)
    }

    fun scanLeDevice(enable: Boolean) {
        when (enable) {
            true -> {
                scanning = true
                bluetoothAdapter.bluetoothLeScanner?.startScan(filters, settings, this)
                viewModel.bleLiveData.value = BleState.BleConnecting
            }
            else -> {
                scanning = false
                bluetoothAdapter.bluetoothLeScanner?.stopScan(this)
            }
        }
    }

    override fun onBatchScanResults(results: MutableList<ScanResult>?) {
        results?.forEach {
            if (it.device?.name.equals(DEVICE_NAME)) {
                it.device?.connectGatt(context, true, gattCallback)
                viewModel.bleLiveData.value = BleState.BleConnected(it.device!!)
            }
        }
    }

    override fun onScanFailed(errorCode: Int) {
        if (errorCode == 1){
            Toast.makeText(context, "Scan already started/Reconnecting...", Toast.LENGTH_LONG).show()
        }
    }

    override fun onScanResult(callbackType: Int, result: ScanResult?) {
        if (result?.device?.name.equals(DEVICE_NAME)) {
            with(result) {
                this?.device?.connectGatt(context, false, gattCallback)
                viewModel.bleLiveData.value = BleState.BleConnected(this?.device!!)
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun stopService() {
        scanLeDevice(false)
        bluetoothAdapter.disable()
        mGatt?.disconnect()
        mGatt?.close()
        mHandler.removeMessages(BLE_FIND_SERVICES)
    }

    companion object {
        val SPERMISSION = arrayOf<String>(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH
        )
        private val DEVICE_NAME = "Nordic_UART"

        private val TX_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e")

        private val RX_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e")

        private val GENERAL_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e")

        private const val CONNECT_DELAY_MS: Long = 700

        private const val BLE_FIND_SERVICES = 0

        // val RX_DESC_UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
    }

    fun checkPermissions(): Boolean {
        SPERMISSION.forEach {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    it
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    fun stopMotor() {
        bleTXCharacteristic?.let {
            mGatt?.setCharacteristicNotification(bleTXCharacteristic, true)
            bleTXCharacteristic?.value = "stop".toByteArray()
            mGatt?.writeCharacteristic(bleTXCharacteristic)
        }
    }

    fun rotate(rotation: String) {
        bleTXCharacteristic?.let {
            mGatt?.setCharacteristicNotification(it, true)
            when (rotation) {
                "rotl" -> {
                    bleTXCharacteristic?.value = "rotl".toByteArray()
                    mGatt?.writeCharacteristic(bleTXCharacteristic)
                }
                "rotr" -> {
                    bleTXCharacteristic?.value = "rotr".toByteArray()
                    mGatt?.writeCharacteristic(bleTXCharacteristic)
                }
                else -> {}
            }
        }
    }

    fun isUIThread(): Boolean = Looper.myLooper() == Looper.getMainLooper()
}
