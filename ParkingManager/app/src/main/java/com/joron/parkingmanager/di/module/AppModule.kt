package com.joron.parkingmanager.di.module

import android.content.Context
import androidx.room.Room
import com.joron.parkingmanager.db.ParkingDb
import com.joron.parkingmanager.networking.ApiClient
import com.joron.parkingmanager.networking.CarRepo
import com.joron.parkingmanager.networking.NetworkService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun provideRoomDb(@ApplicationContext context: Context): ParkingDb {
        return Room.databaseBuilder(context, ParkingDb::class.java, ParkingDb.DB_NAME).build()
    }

    @Singleton
    @Provides
    fun provideNetworkService(): ApiClient {
        return NetworkService.apiClient
    }

    @Singleton
    @Provides
    fun provideCarRepo(@ApplicationContext context: Context) : CarRepo {
        return CarRepo(context)
    }
}