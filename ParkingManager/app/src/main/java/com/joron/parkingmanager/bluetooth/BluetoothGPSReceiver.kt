package com.joron.parkingmanager.bluetooth

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.joron.parkingmanager.MainActivity
import com.joron.parkingmanager.viewmodel.BluetoothLocationViewModel


/**
 * Created by Joro on 25/03/2020
 */
class BluetoothGPSReceiver(
    private val activityContext: FragmentActivity,
    private val viewModel: BluetoothLocationViewModel,
    private val scanner: BluetoothLeScanner
) : BroadcastReceiver(), LifecycleObserver {
    companion object {
        private const val LOCATION_STATE = "android.location.PROVIDERS_CHANGED"
        private const val BLUETOOTH_STATE = BluetoothAdapter.ACTION_STATE_CHANGED
    }

    private val filters = IntentFilter().apply {
        addAction(LOCATION_STATE)
        addAction(BLUETOOTH_STATE)
    }

    init {
        activityContext.lifecycle.addObserver(this@BluetoothGPSReceiver)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun registerReceivers() {
        with(activityContext) {
            registerReceiver(this@BluetoothGPSReceiver, filters)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun unregisterReceivers() {
        with(activityContext) {
            unregisterReceiver(this@BluetoothGPSReceiver)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (scanner.checkPermissions() && context != null && intent != null) {
            when (intent.action) {
                BLUETOOTH_STATE -> {
                    if (MainActivity.isGPSEnabled(context) && BluetoothAdapter.getDefaultAdapter().isEnabled
                        && !BluetoothAdapter.getDefaultAdapter().isDiscovering){
                        scanner.scanLeDevice(true)
                    }
                    return
                }
                LOCATION_STATE -> {
                    val gpsEnabled = MainActivity.isGPSEnabled(context)
                    if (gpsEnabled){
                        viewModel.setGPSToggleValue(true)
                    }
                    return
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                this.activityContext,
                BluetoothLeScanner.SPERMISSIONS,
                MainActivity.PERMISSION_REQUEST_CODE
            )
        }
    }

}