package com.joron.parkingmanager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.joron.parkingmanager.R
import com.joron.parkingmanager.databinding.CarListItemBinding
import com.joron.parkingmanager.handler.CarHandler
import com.joron.parkingmanager.models.Car
import com.joron.parkingmanager.util.NotifiableList
import com.joron.parkingmanager.viewmodel.CarViewModel

/**
 * Created by Joro on 24/08/2020
 */
class CarAdapter(carViewModel: CarViewModel, context: FragmentActivity, private val carHandler: CarHandler) : RecyclerView.Adapter<CarAdapter.CarViewHolder>() {
    val data = NotifiableList<Car, CarAdapter.CarViewHolder>(this)

    init {
        carViewModel.all().observe(context, Observer {
            data.addAndNotify(it)
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DataBindingUtil.inflate<CarListItemBinding>(inflater, R.layout.car_list_item, parent, false )
        return CarViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val car = data[position]
        holder.bind(car)
    }

    inner class CarViewHolder(private val binding: CarListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(car: Car) {
            binding.car = car
            binding.handler = carHandler
            binding.executePendingBindings()
        }
    }
}