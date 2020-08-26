package com.joron.parkingmanager.networking

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/**
 * Created by Joro on 24/08/2020
 */
object NetworkService {
    private const val BASE_URL = "https://parkingstay.azurewebsites.net"
    val apiClient = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .build())
        .client(OkHttpClient())
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder()
                    .setLenient()
                    .create()
            )
        )
        .build()
        .create(ApiClient::class.java)
}