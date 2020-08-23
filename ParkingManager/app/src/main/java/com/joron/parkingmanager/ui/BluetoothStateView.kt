package com.joron.parkingmanager.ui

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.joron.parkingmanager.R
import com.joron.parkingmanager.models.BleState
import kotlinx.android.synthetic.main.bluetooth_indicator.view.*

/**
 * Created by Joro on 15/03/2020
 */
class BluetoothStateView(context: Context, attributeSet: AttributeSet) : LinearLayout(context, attributeSet), View.OnClickListener {
    var iconBluetooth: ImageView
    private var iconCancel: ImageView
    private var message: TextView
    private var messageView: View
    private val statusViewHeight: Float = context.resources.getDimension(R.dimen.message_bar_margin)
    private val mHandler = android.os.Handler(Looper.getMainLooper())
    var contentView: View? = null
    var isVisible = false

    init {
        LayoutInflater.from(context).apply {
            messageView = inflate(R.layout.bluetooth_indicator, null)
            with(messageView) {
                this@BluetoothStateView.addView(this)
                this.setOnClickListener(this@BluetoothStateView)
                val params = this.layoutParams as LinearLayout.LayoutParams
                this.layoutParams = params
                iconBluetooth = findViewById(R.id.activeBluetooth)
                iconCancel = findViewById(R.id.cancelButton)
                iconCancel.setOnClickListener(this@BluetoothStateView)
                message = findViewById(R.id.statusBluetooth)
            }
        }
    }

    private class ViewHideCallback(private val bluetoothStateView: BluetoothStateView, private val showViewDelayed: Boolean, private val show: Boolean) : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {}
        override fun onAnimationCancel(animation: Animator?) {}
        override fun onAnimationStart(animation: Animator?) {}
        override fun onAnimationEnd(animation: Animator?) {
            if (showViewDelayed){
                bluetoothStateView.mHandler.postDelayed(bluetoothStateView::showView, 300)
            }
            bluetoothStateView.isVisible = show
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        getChildAt(0)?.let {
            hideView(true)
        }
    }

    override fun onViewRemoved(child: View?) {
        mHandler.removeCallbacks(this::showView)
        super.onViewRemoved(child)
    }

    private fun hideView(showViewAfterHide: Boolean) {
        translateStatusView(false, showViewAfterHide)
        if (!showViewAfterHide) {
            translateContentView(false)
        }
    }

    private fun translateContentView(show: Boolean) {
        val height = if(show) statusViewHeight else 0f
        contentView?.let {
            ObjectAnimator.ofFloat(it, "translationY", height).apply {
                duration = 300
                start()
            }
        }
    }

    private fun translateStatusView(show: Boolean, showViewAfterHide: Boolean = false) {
        val height = if(show) statusViewHeight else -statusViewHeight
        ObjectAnimator.ofFloat(this, "translationY", height).apply {
            duration = 300
            this.addListener(ViewHideCallback(this@BluetoothStateView, showViewAfterHide, show))
            start()
        }
    }

    fun hideView() = hideView(false)

    fun showView() {
        translateContentView(true)
        translateStatusView(true)
    }

    fun setEnableBluetooth() {
        message.text = "BLUETOOTH NOT ENABLED!\nCLICK THE ICON TO ENABLE IT"
        iconBluetooth.setImageDrawable(context.getDrawable(R.drawable.ic_ble_off))
    }

    fun setConnecting() {
        message.text = "SEARCHING FOR DEVICE\n PLEASE WAIT.."
        iconBluetooth.setImageDrawable(context.getDrawable(R.drawable.ic_ble_searching))
    }

    fun setConnected(name: String) {
        message.text = "DISCOVERING SERVICES OF DEVICE: $name..."
        iconBluetooth.setImageDrawable(context.getDrawable(R.drawable.ic_ble_searching))
    }

    fun setServiceFound(device: BleState.ServiceFound) {
        iconBluetooth.setImageDrawable(context.getDrawable(R.drawable.ic_ble))
        messageView.apply {
            gradientBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.primaryLightColor))
            indicatorBackground.setBackgroundColor(ContextCompat.getColor(context, R.color.primaryLightColor))
            message.text = "CONNECTED TO BLE DEVICE:\t" + device.getDeviceName() + ", " + device.getDeviceAddress()
        }
    }

    fun needsLocation() {
        iconBluetooth.setImageDrawable(context.getDrawable(R.drawable.ic_location_off))
        message.text = "GPS LOCATION IS REQUIRED \nCLICK THE ICON TO ENABLE IT"
    }

    override fun onClick(v: View?) {
        v?.let {
            if (v is ImageView && v.id == R.id.cancelButton) {
                hideView(false)
            }
        }
    }
}

