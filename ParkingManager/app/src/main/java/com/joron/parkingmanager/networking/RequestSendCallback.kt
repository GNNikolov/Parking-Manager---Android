package com.joron.parkingmanager.networking

/**
 * Created by Joro on 27/08/2020
 */
interface RequestSendCallback {
    fun onError(code: Int)
    fun onLoading()
}