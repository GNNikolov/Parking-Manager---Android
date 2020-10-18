package com.joron.parkingmanager.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.joron.parkingmanager.MainActivity
import com.joron.parkingmanager.R
import com.joron.parkingmanager.adapter.CarAdapter
import com.joron.parkingmanager.handler.CarHandler
import com.joron.parkingmanager.models.*
import com.joron.parkingmanager.ui.EmptyRecyclerView
import com.joron.parkingmanager.util.Util
import com.joron.parkingmanager.viewmodel.BleStateViewModel
import com.joron.parkingmanager.viewmodel.CarViewModel
import com.joron.parkingmanager.viewmodel.ParkingStayViewModel
import com.joron.parkingmanager.viewmodel.UserAuthViewModel
import kotlinx.android.synthetic.main.bluetooth_indicator.*

/**
 * Created by Joro on 23/08/2020
 */
class MainFragment : Fragment(), CarHandler {
    private val viewModel: CarViewModel by activityViewModels()
    private val bluetoothViewModel: BleStateViewModel by activityViewModels()
    private val parkingStayViewModel: ParkingStayViewModel by activityViewModels()
    private val authViewModel: UserAuthViewModel by activityViewModels()
    private var selectedCar: Car? = null
    private val observer = Observer<ResponseModel> {
        if (it is ResponseModel.Success && selectedCar != null) {
            selectedCar?.let { car ->
                val isParked = car.isParked
                car.isParked = !isParked
                viewModel.update(car)
                authViewModel.initUser()
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
        activity?.findViewById<FloatingActionButton>(R.id.fab)?.setOnClickListener {
            handleFABClick()
        }
        bluetoothViewModel.bleLiveData.observe(viewLifecycleOwner, Observer {
            if (it is BleState.CharacteristicWritten) {
                selectedCar?.let { car ->
                    val parkingStay = ParkingStay(
                        Util.currentDateFormatted(),
                        if (car.isParked) Event.CHECK_OUT.type else Event.CHECK_IN.type,
                        car.plate
                    )
                    parkingStayViewModel.reportParkingStay(parkingStay)
                        .observe(viewLifecycleOwner, observer)
                }
            }
        })
    }

    private fun handleFABClick() = activity?.let {
        CarPromptDialog.add(it) { carPlate ->
            val car = Car(carPlate)
            viewModel.insert(car)
        }
    }

    override fun onClicked(car: Car) {
        selectedCar = car
        val context: MainActivity? = activity as MainActivity
        context?.let {
            it.bleView.showView()
            showParkingPromptMessage(!car.isParked, it)
        }
    }

    private fun showParkingPromptMessage(enter: Boolean, activity: MainActivity) {
        val title = if (enter) getString(R.string.enter_parking_title)
        else getString(R.string.exit_parking_title)
        val btnText = if (enter) getString(R.string.enter)
        else getString(R.string.exit_btn_text)
        Util.buildDialog(activity, title)
            .setPositiveButton(
                btnText
            ) { _, _ ->
                if (activity.connectedToBleDevice) {
                    activity.sendToBleDevice()
                }
            }.create()
            .show()
    }

    override fun onLongCarClicked(car: Car): Boolean {
        activity?.let {
            CarPromptDialog.delete(it, car) {
                viewModel.delete(car)
            }
        }
        return false
    }
}