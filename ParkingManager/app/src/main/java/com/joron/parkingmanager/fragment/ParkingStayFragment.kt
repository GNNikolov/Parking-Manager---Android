package com.joron.parkingmanager.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joron.parkingmanager.R
import com.joron.parkingmanager.adapter.ParkingStayAdapter
import com.joron.parkingmanager.viewmodel.ParkingStayViewModel

/**
 * Created by Joro on 26/08/2020
 */
class ParkingStayFragment() : Fragment() {
    private val viewModel: ParkingStayViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.parkingstay_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val list =  view as RecyclerView
        list.let {
            it.layoutManager = LinearLayoutManager(it.context, LinearLayoutManager.VERTICAL, false)
            it.addItemDecoration(DividerItemDecoration(view.context, LinearLayoutManager.VERTICAL))
        }
        list.adapter = ParkingStayAdapter(viewModel, requireActivity())
    }
}