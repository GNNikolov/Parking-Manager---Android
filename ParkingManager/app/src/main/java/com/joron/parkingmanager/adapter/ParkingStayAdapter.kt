package com.joron.parkingmanager.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.joron.parkingmanager.R
import com.joron.parkingmanager.databinding.ParkingstayListItemBinding
import com.joron.parkingmanager.models.ParkingStay
import com.joron.parkingmanager.models.Response
import com.joron.parkingmanager.networking.ParkingStayFetchObserver
import com.joron.parkingmanager.viewmodel.ParkingStayViewModel
import java.util.*

/**
 * Created by Joro on 26/08/2020
 */
class ParkingStayAdapter(
    viewModel: ParkingStayViewModel,
    context: FragmentActivity,
    callback: ParkingStayFetchObserver
) : RecyclerView.Adapter<ParkingStayAdapter.ParkingStayViewHolder>() {
    private val data = ArrayList<ParkingStay>()

    init {
        viewModel.fetchParkingStays().observe(context, Observer {
            it?.let {
                when (it) {
                    Response.Loading -> callback.onLoading()
                    is Response.Success -> {
                        data.clear()
                        data.addAll(it.data)
                        notifyDataSetChanged()
                    }
                    is Response.Error -> callback.onError(it.code)
                }
            }
        })
    }

    inner class ParkingStayViewHolder(private val binding: ParkingstayListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ParkingStay) {
            binding.data = data
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParkingStayViewHolder {
        val binding = DataBindingUtil.inflate<ParkingstayListItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.parkingstay_list_item,
            parent,
            false
        )
        return ParkingStayViewHolder(binding)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ParkingStayViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

}