package com.joron.parkingmanager.fragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.joron.parkingmanager.R


/**
 * Created by Joro on 28/08/2020
 */
class CarAddDialog() : DialogFragment() {
    override fun onStart() {
        super.onStart()
        val d = dialog
        if (d != null) {
            d.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            d.window?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(requireContext(), android.R.color.transparent)))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return View.inflate(requireContext(), R.layout.car_add_layout, container)
    }
}