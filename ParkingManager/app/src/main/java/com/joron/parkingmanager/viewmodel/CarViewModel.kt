package com.joron.parkingmanager.viewmodel

import android.app.Application
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.joron.parkingmanager.db.ParkingDb
import com.joron.parkingmanager.models.Car
import com.joron.parkingmanager.networking.CarRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by Joro on 27/08/2020
 */
class CarViewModel @ViewModelInject
constructor(private val db: ParkingDb,
            application: Application) : AndroidViewModel(application) {

    private val carRepo = CarRepo(application)

    fun insert(car: Car) = viewModelScope.launch(Dispatchers.IO) {
        db.getCarDao().insert(car)
    }

    fun insertAll(data: List<Car>) = viewModelScope.launch(Dispatchers.IO) {
        db.getCarDao().insertAll(data)
    }

    fun update(car: Car) = viewModelScope.launch(Dispatchers.IO) {
        db.getCarDao().update(car)
    }

    fun delete(car: Car) = viewModelScope.launch(Dispatchers.IO) {
        db.getCarDao().delete(car)
    }

    fun all(): LiveData<List<Car>> = db.getCarDao().getAll()

    fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        db.getCarDao().deleteAll()
    }

    fun postCar(car: Car) = carRepo.post(car)

    fun fetchAll() = carRepo.get()

}