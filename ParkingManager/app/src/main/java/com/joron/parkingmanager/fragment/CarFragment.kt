package com.joron.parkingmanager.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.joron.parkingmanager.MainActivity
import com.joron.parkingmanager.R
import com.joron.parkingmanager.adapter.CarAdapter
import com.joron.parkingmanager.handler.CarHandler
import com.joron.parkingmanager.models.Car
import com.joron.parkingmanager.ui.EmptyRecyclerView
import com.joron.parkingmanager.viewmodel.CarViewModel
import kotlinx.android.synthetic.main.bluetooth_indicator.*

/**
 * Created by Joro on 23/08/2020
 */
class CarFragment : Fragment(), CarHandler {
    private val viewModel: CarViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.cars_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val carAdapter = CarAdapter(viewModel, activity!!, this)
        view.findViewById<EmptyRecyclerView>(R.id.carList)?.let {
            it.emptyView = view.findViewById(R.id.emptyCarsView)
            it.layoutManager = GridLayoutManager(requireContext(), 2)
            it.adapter = carAdapter
        }
        activity?.findViewById<FloatingActionButton>(R.id.fab)?.setOnClickListener{
            handleFABClick()
        }
    }

    private fun handleFABClick() = activity?.let {
        CarEditDialog.add(it) { carPlate ->
            val car = Car(carPlate)
            viewModel.insert(car)
        }
    }

    override fun onClicked(car: Car) {
        val context: MainActivity? = activity as MainActivity
        context?.let {
            it.bleView.showView()
            if (it.connectedToBleDevice) {
                it.sendToBleDevice()
            }
        }
    }

    override fun onLongCarClicked(car: Car): Boolean {
        activity?.let {
            CarEditDialog.delete(it, car) {
                viewModel.delete(car)
            }
        }
        return false
    }
}