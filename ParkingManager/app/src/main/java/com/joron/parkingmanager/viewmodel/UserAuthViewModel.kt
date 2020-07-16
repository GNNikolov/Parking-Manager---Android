package com.joron.parkingmanager.viewmodel

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * Created by Joro on 14/07/2020
 */
class UserAuthViewModel(private val context: Application) : AndroidViewModel(context) {

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
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

    fun handleSignIn(resultCode: Int) =
        if (resultCode == Activity.RESULT_OK)
            initUser()
        else
            _userLiveData.value = null


    fun handleSignOut() {
        AuthUI.getInstance().signOut(context).addOnCompleteListener {
            if (it.isSuccessful) {
                _userLiveData.value = null
            }
        }.addOnCanceledListener(::initUser)
    }

}