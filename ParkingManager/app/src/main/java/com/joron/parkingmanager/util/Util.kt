package com.joron.parkingmanager.util

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import com.joron.parkingmanager.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Joro on 24/08/2020
 */
object Util {
    private const val TOKEN_STORE = "tokenStore"
    private const val FCM_TOKEN_KEY = "fcmTokenKey"
    private const val JWT_AUTH = "jwt"

    //return now date in format: "yyyy-MM-dd'T'HH:mm:ss
    fun currentDateFormatted(dateTime: Long = System.currentTimeMillis()): String {
        val pattern = "yyyy-MM-dd HH:mm:ss"
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(dateTime)
    }

    fun parseFormattedDate(formatted: String): Date? {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return format.parse(formatted)
    }

    fun getDateAndTimeFormatted(formatted: String, context: Context): String {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date = format.parse(formatted) ?: return formatted
        val calendar = Calendar.getInstance()
        calendar.time = date
        val monthDateFormat = SimpleDateFormat("MMM", Locale.getDefault()).format(calendar.time)
        val timeDateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(calendar.time)
        val year = calendar.get(Calendar.YEAR)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return context.getString(R.string.date_time_formatted, day, monthDateFormat, year, timeDateFormat)
    }

    fun getFCMToken(context: Context): String? =
        context.getSharedPreferences(TOKEN_STORE, Context.MODE_PRIVATE)
            ?.getString(FCM_TOKEN_KEY, null)

    fun storeFCMToken(context: Context, token: String) {
        context.getSharedPreferences(TOKEN_STORE, Context.MODE_PRIVATE)?.edit()?.putString(FCM_TOKEN_KEY, token)?.apply()
    }

    fun storeJWTToken(context: Context, jwt: String?) {
        context.getSharedPreferences(TOKEN_STORE, Context.MODE_PRIVATE)?.edit()?.putString(JWT_AUTH, jwt)?.apply()
    }

    fun getJWTToken(context: Context): String? =
        context.getSharedPreferences(TOKEN_STORE, Context.MODE_PRIVATE)
            ?.getString(JWT_AUTH, null)

    fun buildDialog(context: FragmentActivity, title: String, message: String? = null): AlertDialog.Builder {
        return AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
    }
}