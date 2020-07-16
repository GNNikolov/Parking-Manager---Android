package com.joron.parkingmanager.authentication

import androidx.fragment.app.FragmentActivity
import com.firebase.ui.auth.AuthUI
import com.joron.parkingmanager.viewmodel.UserAuthViewModel

/**
 * Created by Joro on 14/07/2020
 */
class FirebaseAuthManager(private val context: FragmentActivity, private val authViewModel: UserAuthViewModel) {

    private val providers = arrayListOf(
        AuthUI.IdpConfig.PhoneBuilder().build()
    )

    fun signIn() = context.startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build(), RC_SIGN_IN)


    fun handleSignInResult(requestCode: Int, resultCode: Int) {
        if (requestCode != RC_SIGN_IN) {
            return
        }
       authViewModel.handleSignIn(resultCode)
    }

    fun signOut() = authViewModel.handleSignOut()

    companion object {
        private const val RC_SIGN_IN = 7
    }

}