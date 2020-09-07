package com.joron.parkingmanager.viewmodel

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.joron.parkingmanager.models.Customer
import com.joron.parkingmanager.models.ResponseModel
import com.joron.parkingmanager.models.SignInResponseModel
import com.joron.parkingmanager.networking.NetworkService
import com.joron.parkingmanager.util.Util
import kotlinx.coroutines.Dispatchers
import retrofit2.Response

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

    val userLoadLiveData: LiveData<FirebaseUser?>
        get() = _userLiveData

    init {
        Log.i("ViewModelInit", this.toString())
    }

    fun initUser() {
        _userLiveData.value = auth.currentUser
    }

    fun handleSignIn(resultCode: Int) = liveData(Dispatchers.IO) {
        val user = auth.currentUser
        val phone = user?.phoneNumber
        val uId = user?.uid
        if (resultCode == Activity.RESULT_OK && phone != null && fcmToken != null && uId != null) {
            val customer = Customer(phone, fcmToken!!, uId)
            var response: Response<String>? = null
            try {
                emit(ResponseModel.Loading)
                response = apiService.postCustomer(customer)
                val body = response.body()
                if (response.isSuccessful && body != null) {
                    Util.storeJWTToken(context, body)
                    emit(SignInResponseModel(user))
                    _userLiveData.postValue(user)
                } else {
                    _userLiveData.postValue(null)
                    emit(ResponseModel.Error(response.code()))
                }
            } catch (e: Exception) {
                emit(ResponseModel.Error(response?.code() ?: -1))
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