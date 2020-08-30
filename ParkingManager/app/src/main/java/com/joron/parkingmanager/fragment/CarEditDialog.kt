package com.joron.parkingmanager.fragment

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.joron.parkingmanager.R
import com.joron.parkingmanager.models.Car


/**
 * Created by Joro on 28/08/2020
 */
class CarEditDialog : DialogFragment() {
    private var onInputDone: ((plate: String) -> Unit)? = null
    private var onCarDeleteClick: (() -> Unit?)? = null
    private var dialogType = -1
    private var carToDelete: Car? = null

    override fun onStart() {
        super.onStart()
        val d = dialog
        if (d != null) {
            d.window?.setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            d.window?.setBackgroundDrawable(
                ColorDrawable(
                    ContextCompat.getColor(
                        requireContext(),
                        android.R.color.transparent
                    )
                )
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return View.inflate(requireContext(), R.layout.car_edit_layout, container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var title = " "
        val editText = view.findViewById<AppCompatEditText>(R.id.plateInput)
        var btnText = " "
        when (dialogType) {
            ADD -> {
                title = getString(R.string.add_car)
                btnText = getString(R.string.done)
            }
            DELETE -> {
                title = getString(R.string.delete_car)
                editText.setText(carToDelete?.plate)
                editText.isEnabled = false
                btnText = getString(R.string.delete)
            }
        }
        view.findViewById<TextView>(R.id.carEditTitle).text = title
        val btn = view.findViewById<Button>(R.id.addCarBtn)
        btn.text = btnText
        btn.setOnClickListener {
            val input = editText.text?.toString() ?: "/0"
            onInputDone?.invoke(input)
            onCarDeleteClick?.invoke()
            dismiss()
        }
        editText.requestFocus()
    }

    companion object {
        private const val TAG = "carAddDialog"
        private const val ADD = 1
        private const val DELETE = 2
        fun add(context: FragmentActivity, onInputDone: (plate: String) -> Unit) {
            CarEditDialog().apply {
                this.dialogType = ADD
                this.onInputDone = onInputDone
                show(context.supportFragmentManager, TAG)
            }
        }

        fun delete(context: FragmentActivity, car: Car, onDeleted: () -> Unit) {
            CarEditDialog().apply {
                this.dialogType = DELETE
                this.carToDelete = car
                this.onCarDeleteClick = onDeleted
                show(context.supportFragmentManager, TAG)
            }
        }
    }
}