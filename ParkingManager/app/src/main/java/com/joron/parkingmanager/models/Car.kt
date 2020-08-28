package com.joron.parkingmanager.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Joro on 27/08/2020
 */
@Entity
data class Car(@PrimaryKey(autoGenerate = true) val id: Int, val plate: String, var isParked: Boolean = false)