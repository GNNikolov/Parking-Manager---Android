package com.joron.parkingmanager.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.joron.parkingmanager.R
import com.joron.parkingmanager.adapter.CarAdapter
import kotlinx.android.synthetic.main.cars_fragment.*

/**
 * Created by Joro on 23/08/2020
 */
class CarCollectionFragment() : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.cars_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        carList.layoutManager = GridLayoutManager(requireContext(), 2)
        val adapter = CarAdapter()
        carList.adapter = adapter
    }
}