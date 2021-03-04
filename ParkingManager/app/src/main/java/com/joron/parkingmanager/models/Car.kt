package com.joron.parkingmanager.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Joro on 27/08/2020
 */
@Entity
class Car(val plate: String, var onParking: Boolean = false) {
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}