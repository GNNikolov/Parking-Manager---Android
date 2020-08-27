package com.joron.parkingmanager.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.joron.parkingmanager.R
import com.joron.parkingmanager.adapter.ParkingStayAdapter
import com.joron.parkingmanager.networking.ParkingStayFetchObserver
import com.joron.parkingmanager.ui.EmptyRecyclerView
import com.joron.parkingmanager.viewmodel.ParkingStayViewModel

/**
 * Created by Joro on 26/08/2020
 */
class ParkingStayFragment : Fragment(), ParkingStayFetchObserver {
    private val viewModel: ParkingStayViewModel by activityViewModels()
    private lateinit var messageView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.parkingstay_fragment, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val list =  view.findViewById<EmptyRecyclerView>(R.id.parkingStayList)
        list.let {
            messageView = view.findViewById(R.id.emptyView)
            it.emptyView = messageView
            it.layoutManager = LinearLayoutManager(it.context, LinearLayoutManager.VERTICAL, false)
            it.addItemDecoration(DividerItemDecoration(view.context, LinearLayoutManager.VERTICAL))
        }
        list.adapter = ParkingStayAdapter(viewModel, requireActivity(), this)
    }

    override fun onError(code: Int) {
        messageView.text = "Error: $code"
    }

    override fun onLoading() {
        messageView.text = getString(R.string.loading)
    }
}