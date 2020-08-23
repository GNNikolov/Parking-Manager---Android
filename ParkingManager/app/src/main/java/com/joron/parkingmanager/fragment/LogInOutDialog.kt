package com.joron.parkingmanager.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.joron.parkingmanager.R

/**
 * Created by Joro on 23/08/2020
 */
class LogInOutDialog() : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return View.inflate(requireContext(), R.layout.log_in_out_bottom_sheet, container)
    }
}