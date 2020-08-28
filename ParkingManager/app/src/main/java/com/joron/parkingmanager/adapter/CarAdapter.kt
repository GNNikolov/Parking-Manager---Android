package com.joron.parkingmanager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joron.parkingmanager.R

/**
 * Created by Joro on 24/08/2020
 */
class CarAdapter() : RecyclerView.Adapter<CarAdapter.CarViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.car_list_item, parent, false)
        return CarViewHolder(view)
    }

    override fun getItemCount(): Int = 0

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {

    }

    inner class CarViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    }
}