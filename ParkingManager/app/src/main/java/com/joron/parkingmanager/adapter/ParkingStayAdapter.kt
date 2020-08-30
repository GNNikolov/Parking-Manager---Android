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
import com.joron.parkingmanager.models.ParkingStayResponseModel
import com.joron.parkingmanager.models.ResponseModel
import com.joron.parkingmanager.networking.RequestSendCallback
import com.joron.parkingmanager.util.NotifiableList
import com.joron.parkingmanager.viewmodel.ParkingStayViewModel

/**
 * Created by Joro on 26/08/2020
 */
class ParkingStayAdapter(
    viewModel: ParkingStayViewModel,
    context: FragmentActivity,
    callback: RequestSendCallback
) : RecyclerView.Adapter<ParkingStayAdapter.ParkingStayViewHolder>() {
    private val data = NotifiableList<ParkingStay, ParkingStayAdapter.ParkingStayViewHolder>(this)

    init {
        viewModel.fetchParkingStays().observe(context, Observer {
            it?.let {
                when (it) {
                    ResponseModel.Loading -> callback.onLoading()
                    is ParkingStayResponseModel -> {
                        data.addAndNotify(it.data)
                    }
                    is ResponseModel.Error -> callback.onError(it.code)
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