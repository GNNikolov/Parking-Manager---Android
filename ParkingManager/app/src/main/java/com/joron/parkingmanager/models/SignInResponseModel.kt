package com.joron.parkingmanager.models

import com.google.firebase.auth.FirebaseUser

/**
 * Created by Joro on 28/08/2020
 */
class SignInResponseModel(val data: FirebaseUser) : ResponseModel.Success()