package com.joron.parkingmanager.util

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
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
    fun currentDateFormatted(): String {
        val pattern = "yyyy-MM-dd'T'HH:mm:ss"
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return sdf.format(System.currentTimeMillis())
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