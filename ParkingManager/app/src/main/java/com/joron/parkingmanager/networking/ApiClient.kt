package com.joron.parkingmanager.networking

import com.joron.parkingmanager.models.Customer
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Created by Joro on 24/08/2020
 */
interface ApiClient {
    @POST("/api/v2/post")
    suspend fun postCustomer(@Body customer: Customer) : Response<String>
}