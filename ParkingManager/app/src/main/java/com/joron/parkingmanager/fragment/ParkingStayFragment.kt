package com.joron.parkingmanager.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.joron.parkingmanager.R
import com.joron.parkingmanager.adapter.ParkingStayAdapter
import com.joron.parkingmanager.models.ParkingStayResponseModel
import com.joron.parkingmanager.models.ResponseModel
import com.joron.parkingmanager.ui.EmptyRecyclerView
import com.joron.parkingmanager.viewmodel.ParkingStayViewModel
import com.joron.parkingmanager.viewmodel.UserAuthViewModel

/**
 * Created by Joro on 26/08/2020
 */
class ParkingStayFragment : Fragment() {

    private val viewModel: ParkingStayViewModel by activityViewModels()
    private val userAuthViewModel: UserAuthViewModel by activityViewModels()
    private var messageView: TextView? = null
    private val adapter = ParkingStayAdapter()
    private val observer = Observer<ResponseModel> {
        it?.let {
            when (it) {
                ResponseModel.Loading -> messageView?.text = getString(R.string.loading)
                is ParkingStayResponseModel -> {
                    adapter.data.addAndNotify(it.data)
                }
                is ResponseModel.Error -> messageView?.text = "Error: $it.code"
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.parkingstay_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val list = view.findViewById<EmptyRecyclerView>(R.id.parkingStayList)
        list.let {
            messageView = view.findViewById(R.id.emptyView)
            it.emptyView = messageView
            it.layoutManager = LinearLayoutManager(it.context, LinearLayoutManager.VERTICAL, false)
            it.addItemDecoration(DividerItemDecoration(view.context, LinearLayoutManager.VERTICAL))
            list.adapter = adapter
        }
        userAuthViewModel.userLoadLiveData.observe(viewLifecycleOwner, Observer { user ->
            if (user != null) {
                viewModel.fetchParkingStays().observe(viewLifecycleOwner, observer)
            }
        })
    }

}