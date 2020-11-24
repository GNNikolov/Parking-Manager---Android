package com.joron.parkingmanager.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.os.postDelayed
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.joron.parkingmanager.R
import com.joron.parkingmanager.models.State
import com.joron.parkingmanager.util.Util
import com.joron.parkingmanager.viewmodel.BluetoothLocationViewModel

/**
 * Created by Joro on 09/03/2020
 */
class BluetoothLeScanner(
    private val context: FragmentActivity,
    private val viewModel: BluetoothLocationViewModel
) : ScanCallback(), LifecycleObserver {

    private var scanning: Boolean = false
    private val filter = ScanFilter.Builder().setDeviceName(DEVICE_NAME).build()
    private val filters = arrayListOf<ScanFilter>(filter)
    private val settings = ScanSettings.Builder().build()
    private val gattCallback = BluetoothLeGATTConnector(this, viewModel)
    private var mDevice: BluetoothDevice? = null
    private val bluetoothAdapter: BluetoothAdapter? by lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager =
            context.application.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }
    private val mHandler = Handler(Looper.getMainLooper())
    val isEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled ?: false

    init {
        context.lifecycle.addObserver(this)
    }

    fun scanLeDevice(enable: Boolean) {
        when (enable) {
            true -> {
                scanning = true
                bluetoothAdapter?.bluetoothLeScanner?.startScan(filters, settings, this)
                viewModel.stateLiveData.value = State.BleConnecting
                mHandler.postDelayed({
                    bluetoothAdapter?.bluetoothLeScanner?.stopScan(this)
                    if (mDevice == null) {
                        showNoBleDevicesFoundDialog()
                    }
                }, SCAN_STOP_DELAY)
            }
            else -> {
                scanning = false
                bluetoothAdapter?.bluetoothLeScanner?.stopScan(this)
            }
        }
    }

    fun rotate() = gattCallback.rotate()

    override fun onScanFailed(errorCode: Int) {
        if (errorCode == 1) {
            Toast.makeText(context, "Scan already started/Reconnecting...", Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun onScanResult(callbackType: Int, result: ScanResult?) {
        if (result?.device?.name.equals(DEVICE_NAME) && mDevice == null) {
            mDevice = result?.device?.also {
                it.connectGatt(context, false, gattCallback)
                viewModel.stateLiveData.value = State.BleConnected(it)
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun stopService() {
        scanLeDevice(false)
        bluetoothAdapter?.disable()
        gattCallback.close()
    }

    companion object {
        val SPERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH
        )
        private const val DEVICE_NAME = "Nordic_UART"
        private const val SCAN_STOP_DELAY: Long = 5000
    }

    fun checkPermissions(): Boolean {
        SPERMISSIONS.forEach {
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

    private fun showNoBleDevicesFoundDialog() {
        val title = context.getString(R.string.no_ble_devices_found)
        val message = context.getString(R.string.scan_again)
        Util.buildDialog(context, title, message).also { builder ->
            builder.setCancelable(false)
            builder.setPositiveButton(context.getString(R.string.ok)) { _, _ ->
                scanLeDevice(true)
            }
            builder.setNegativeButton(context.getString(R.string.cancel), null)
        }.create().show()
    }
}