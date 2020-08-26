package com.joron.parkingmanager.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.joron.parkingmanager.models.Customer
import com.joron.parkingmanager.networking.NetworkService
import com.joron.parkingmanager.service.FCMService
import com.joron.parkingmanager.util.Util
import kotlinx.coroutines.launch

/**
 * Created by Joro on 14/07/2020
 */
class UserAuthViewModel(private val context: Application) : AndroidViewModel(context) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val apiService by lazy {
        NetworkService.apiClient
    }
    private val fcmToken by lazy {
        Util.getFCMToken(context)
    }
    private val _userLiveData = MutableLiveData<FirebaseUser?>()

    val userLiveData: LiveData<FirebaseUser?>
        get() = _userLiveData

    init {
        initUser()
    }

    fun initUser() {
        _userLiveData.value = auth.currentUser
    }

    fun handleSignIn(resultCode: Int) {
        if (resultCode == Activity.RESULT_OK) {
            val phone = auth.currentUser?.phoneNumber
            if (phone != null && fcmToken != null) {
                val customer = Customer(phone, fcmToken!!)
                try {
                    viewModelScope.launch {
                        val response = apiService.postCustomer(customer)
                        val body = response.body()
                        if (response.isSuccessful && body != null) {
                            Util.storeJWTToken(context, body)
                            initUser()
                        } else {
                            _userLiveData.value = null
                        }
                    }
                } catch (e: Exception) {
                    _userLiveData.value = null
                }
            }
        } else {
            _userLiveData.value = null
        }
    }

    fun handleSignOut() {
        AuthUI.getInstance().signOut(context).addOnCompleteListener {
            if (it.isSuccessful) {
                Util.storeJWTToken(context, null)
                _userLiveData.value = null
            }
        }.addOnCanceledListener(::initUser)
    }

}