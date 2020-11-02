package com.joron.parkingmanager.viewmodel

import android.app.Application
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.AndroidViewModel
import com.joron.parkingmanager.R
import com.joron.parkingmanager.models.ParkingStay
import com.joron.parkingmanager.networking.ParkingStayRepo
import com.joron.parkingmanager.util.Util
import java.util.concurrent.TimeUnit

/**
 * Created by Joro on 26/08/2020
 */
class ParkingStayViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = ParkingStayRepo(application.applicationContext)

    fun fetchParkingStays() = repo.get()

    fun enterParking(parkingStay: ParkingStay) = repo.post(parkingStay)

    fun exitParking(parkingStay: ParkingStay) = repo.update(parkingStay)

    companion object {
        @JvmStatic
        @BindingAdapter("android:text")
        fun setView(view: TextView, data: ParkingStay) {
            val context = view.context
            val dateTimeExitStr = data.dateTimeExited ?: return
            val dateTimeEnterStr = data.dateTimeEntered

            val dateTimeEnter = Util.parseFormattedDate(dateTimeEnterStr) ?: return
            val dateTimeExit = Util.parseFormattedDate(dateTimeExitStr) ?: return

            val timeStayed = dateTimeExit.time - dateTimeEnter.time
            val daysSpent = TimeUnit.DAYS.convert(timeStayed, TimeUnit.MILLISECONDS)
            val minutesSpent = TimeUnit.MINUTES.convert(timeStayed, TimeUnit.MILLISECONDS)
            val hoursSpent = TimeUnit.HOURS.convert(timeStayed, TimeUnit.MILLISECONDS)
            val seconds = TimeUnit.SECONDS.convert(timeStayed, TimeUnit.MILLISECONDS)
            when {
                daysSpent > 0 -> context.getString(R.string.days, daysSpent)
                hoursSpent > 0 -> context.getString(R.string.hours, hoursSpent)
                minutesSpent > 0 -> context.getString(R.string.minutes, minutesSpent)
                seconds > 0 -> context.getString(R.string.seconds, seconds)
                else -> null
            }?.also { msg ->
                view.text = context.getString(R.string.parking_spent_time, msg)
            }
        }
    }
}