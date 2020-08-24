package com.joron.parkingmanager.service

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Created by Joro on 10/06/2020
 */
class FCMService : FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        val pref: SharedPreferences? = getSharedPreferences(FCM_TOKEN_STORE, Context.MODE_PRIVATE)
        pref?.edit()?.putString(FCM_TOKEN_KEY, p0)?.apply()
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        //TODO handle message coming when app is running(not killed)!!!
        Log.d("FCM", p0.toString())
    }

    companion object {
        const val FCM_TOKEN_STORE = "fcmTokenStore"
        const val FCM_TOKEN_KEY = "fcmTokenKey"
    }

}