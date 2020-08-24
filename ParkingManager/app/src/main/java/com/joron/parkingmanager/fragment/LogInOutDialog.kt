package com.joron.parkingmanager.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.joron.parkingmanager.R
import kotlinx.android.synthetic.main.log_in_out_bottom_sheet.*

/**
 * Created by Joro on 23/08/2020
 */
class LogInOutDialog private constructor() : BottomSheetDialogFragment() {
    private var action: Int = -1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return View.inflate(requireContext(), R.layout.log_in_out_bottom_sheet, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        when(action) {
            SIGN_IN -> {
                dialogTitle.text = getString(R.string.not_signed_in)
                button_sign_in_out.text = getString(R.string.sign_in)
            }
            SIGN_OUT ->{
                dialogTitle.text = getString(R.string.signing_out)
                button_sign_in_out.text = getString(R.string.exit)
            }
        }
    }

    companion object {
        private fun init() = LogInOutDialog()
        fun showSignInDialog(activity: FragmentActivity) = init().apply {
            action = SIGN_IN
            show(activity.supportFragmentManager, TAG)
        }

        fun showSignOutDialog(activity: FragmentActivity) = init().apply {
            action = SIGN_OUT
            show(activity.supportFragmentManager, TAG)
        }
        private const val TAG = "signInOutDialog"
        private const val SIGN_IN = 0
        private const val SIGN_OUT = 1
    }

}