package com.joron.parkingmanager.ui

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.os.Looper
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.joron.parkingmanager.R
import com.joron.parkingmanager.models.BleState
import kotlinx.android.synthetic.main.bluetooth_indicator.view.*

/**
 * Created by Joro on 15/03/2020
 */
class BleStatusLayout(context: Context, attributeSet: AttributeSet) : FrameLayout(context, attributeSet),
    View.OnClickListener {
    lateinit var iconBluetooth: ImageView
    private lateinit var iconCancel: ImageView
    private lateinit var message: TextView
    private lateinit var messageView: View
    private val messageBarMargin: Float = context.resources.getDimension(R.dimen.message_bar_margin)
    private val mHandler = android.os.Handler(Looper.getMainLooper())
    var contentView: View ?= null

    private class ViewHideCallback(private val bleStatusLayout: BleStatusLayout, private val showViewDelayed: Boolean) : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {}
        override fun onAnimationCancel(animation: Animator?) {}
        override fun onAnimationStart(animation: Animator?) {}
        override fun onAnimationEnd(animation: Animator?) {
            if (showViewDelayed){
                bleStatusLayout.mHandler.postDelayed(bleStatusLayout::showView, 300)
            }
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        getChildAt(0)?.let {
            hideView(true)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        LayoutInflater.from(context).apply {
            messageView = inflate(R.layout.bluetooth_indicator, null)
            with(messageView) {
                this@BleStatusLayout.addView(this)
                this.setOnClickListener(this@BleStatusLayout)
                val params = this.layoutParams as LayoutParams
                params.topMargin = messageBarMargin.toInt()
                this.layoutParams = params
                iconBluetooth = findViewById(R.id.activeBluetooth)
                iconCancel = findViewById(R.id.cancelButton)
                iconCancel.setOnClickListener(this@BleStatusLayout)
                message = findViewById(R.id.statusBluetooth)
            }
        }
    }


    override fun onViewRemoved(child: View?) {
        mHandler.removeCallbacks(this::showView)
        super.onViewRemoved(child)
    }

    private fun hideView(showViewDelayed: Boolean) {
        ObjectAnimator.ofFloat(this, "translationY", -messageView.height.toFloat()).apply {
            duration = 300
            this.addListener(ViewHideCallback(this@BleStatusLayout, showViewDelayed))
            start()
        }
        if (!showViewDelayed){
            contentView?.let {
                ObjectAnimator.ofFloat(it, "translationY", 0f).apply {
                    duration = 300
                    start()
                }
            }
        }
    }

    private fun showView() {
        messageView.visibility = View.VISIBLE
        ObjectAnimator.ofFloat(this, "translationY", 0f).apply {
            duration = 300
            start()
        }
        contentView?.let {
            ObjectAnimator.ofFloat(it, "translationY", messageView.height.toFloat()).apply {
                duration = 300
                start()
            }
        }
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
        message.text = "PLEASE ENABLE GPS LOCATION\nIN ORDER TO USE BLUETOOTH"
    }

    override fun onClick(v: View?) {
        v?.let {
            if (v is ImageView && v.id == R.id.cancelButton) {
                hideView(false)
            }
        }
    }
}

