package com.joron.parkingmanager.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.joron.parkingmanager.dao.CarDao
import com.joron.parkingmanager.models.Car

/**
 * Created by Joro on 27/08/2020
 */
@Database(entities = [Car::class], version = 1)
abstract class ParkingDb : RoomDatabase() {
    abstract fun getCarDao() : CarDao

    companion object{
        const val DB_NAME = "parkingDb"
    }
}