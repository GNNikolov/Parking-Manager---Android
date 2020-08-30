package com.joron.parkingmanager.fragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.joron.parkingmanager.R


/**
 * Created by Joro on 28/08/2020
 */
class CarAddDialog : DialogFragment() {
    private  var onInputDone: ((plate: String) -> Unit)? = null

    override fun onStart() {
        super.onStart()
        val d = dialog
        if (d != null) {
            d.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val editText = view.findViewById<AppCompatEditText>(R.id.plateInput)
        view.findViewById<Button>(R.id.addCarBtn).setOnClickListener {
            val input = editText.text?.toString() ?: "/0"
            onInputDone?.invoke(input)
            dismiss()
        }
    }

    companion object {
        private const val TAG = "carAddDialog"
        fun show(context: FragmentActivity, onInputDone: (plate: String) -> Unit) {
            CarAddDialog().apply {
                this.onInputDone = onInputDone
                show(context.supportFragmentManager, TAG)
            }
        }
    }
}