package com.joron.parkingmanager.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.joron.parkingmanager.models.Car

/**
 * Created by Joro on 27/08/2020
 */
@Dao
interface CarDao {
    @Insert
    fun insert(car: Car)

    @Update
    fun update(car: Car)

    @Delete
    fun delete(car: Car)

    @Query("SELECT * FROM car")
    fun getAll(): LiveData<List<Car>>

    @Query("DELETE  FROM car")
    fun deleteAll()
}