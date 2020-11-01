package com.joron.parkingmanager.ui

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.joron.parkingmanager.R
import com.joron.parkingmanager.models.State
import kotlinx.android.synthetic.main.bluetooth_indicator.view.*

/**
 * Created by Joro on 15/03/2020
 */
class BluetoothStateView(context: Context, attributeSet: AttributeSet) :
    LinearLayout(context, attributeSet) {
    var iconAction: ImageView? = null
    var statusText: TextView? = null
    private var statusViewHeight: Float = context.resources.getDimension(R.dimen.message_bar_margin)
    var contentView: LinearLayout? = null
    var isVisible = false

    private class ViewHideCallback(
        private val bluetoothStateView: BluetoothStateView,
        private val show: Boolean
    ) : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {}
        override fun onAnimationCancel(animation: Animator?) {}
        override fun onAnimationStart(animation: Animator?) {}
        override fun onAnimationEnd(animation: Animator?) {
            bluetoothStateView.isVisible = show
        }
    }

    fun initViews(contentView: LinearLayout, iconAction: ImageView, statusTextView: TextView) {
        this.contentView = contentView
        this.iconAction = iconAction
        this.statusText = statusTextView
    }

    private fun translateStatusView(show: Boolean) {
        val height = if (show) statusViewHeight else 0f
        ObjectAnimator.ofFloat(contentView, "translationY", height).apply {
            duration = 300
            this.addListener(ViewHideCallback(this@BluetoothStateView, show))
            start()
        }
    }

    fun hide() {
        if (isVisible)
            translateStatusView(false)
    }


    fun showView() {
        if (!isVisible)
            translateStatusView(true)
    }

    fun setEnableBluetooth() {
        statusText?.text = context.getString(R.string.enable_bluetooth_message)
        iconAction?.setImageDrawable(context.getDrawable(R.drawable.ic_ble_off))
    }

    fun setConnecting() {
        statusText?.text = context.getString(R.string.searching_for_ble_devices)
        iconAction?.setImageDrawable(context.getDrawable(R.drawable.ic_ble_searching))
    }

    fun setConnected(name: String) {
        statusText?.text = context.getString(R.string.discovering_services_of_ble_device, name)
        iconAction?.setImageDrawable(context.getDrawable(R.drawable.ic_ble_searching))
    }

    fun setServiceFound(device: State.ServiceFound) {
        iconAction?.setImageDrawable(context.getDrawable(R.drawable.ic_ble))
        indicatorBackground.setBackgroundColor(
            ContextCompat.getColor(
                context,
                R.color.primaryLightColor
            )
        )
        statusText?.text =
            context.getString(R.string.connected_to_ble_device, device.getDeviceName())
    }

    fun needsLocation() {
        iconAction?.setImageDrawable(context.getDrawable(R.drawable.ic_location_off))
        statusText?.text = context.getString(R.string.enable_location_message)
    }
}

