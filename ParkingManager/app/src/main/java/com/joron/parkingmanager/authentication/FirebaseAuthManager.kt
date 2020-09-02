package com.joron.parkingmanager.authentication

import androidx.fragment.app.FragmentActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.joron.parkingmanager.viewmodel.CarViewModel
import com.joron.parkingmanager.viewmodel.UserAuthViewModel
import java.util.*

/**
 * Created by Joro on 14/07/2020
 */
class FirebaseAuthManager(private val context: FragmentActivity,
                          private val authViewModel: UserAuthViewModel,
                          private val carViewModel: CarViewModel) {
    private val firebaseAuth = FirebaseAuth.getInstance()
    val firebaseAuthSettings = firebaseAuth.firebaseAuthSettings.also {
        it.setAutoRetrievedSmsCodeForPhoneNumber("+359886556085", "293233")
    }


    private val providers = arrayListOf(
        AuthUI.IdpConfig.PhoneBuilder()
            .setWhitelistedCountries(Collections.singletonList("BG"))
            .build()
    )

    fun signIn() = context.startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(), RC_SIGN_IN)


    fun signOut() {
        authViewModel.handleSignOut()
        carViewModel.deleteAll()
    }

    companion object {
        const val RC_SIGN_IN = 7
    }

}