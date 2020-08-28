package com.joron.parkingmanager.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.joron.parkingmanager.db.ParkingDb
import com.joron.parkingmanager.db.ParkingDb.Companion.DB_NAME
import com.joron.parkingmanager.models.Car
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Joro on 27/08/2020
 */
class CarViewModel(application: Application) : AndroidViewModel(application) {
    private val db by lazy {
        Room.databaseBuilder(application, ParkingDb::class.java, DB_NAME).build()
    }

    fun insert(car: Car) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            db.getCarDao().insert(car)
        }
    }

    fun update(car: Car) = viewModelScope.launch {
        withContext(Dispatchers.IO){
            db.getCarDao().update(car)
        }
    }

    fun delete(car: Car) = viewModelScope.launch {
        withContext(Dispatchers.IO){
            db.getCarDao().delete(car)
        }
    }

    fun all() = db.getCarDao().getAll()

}