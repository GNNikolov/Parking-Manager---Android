package com.joron.parkingmanager.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.joron.parkingmanager.util.Util

/**
 * Created by Joro on 10/06/2020
 */
class FCMService : FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Util.storeFCMToken(applicationContext, p0)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        //TODO handle message coming when app is running(not killed)!!!
        Log.d("FCM", p0.toString())
    }

}