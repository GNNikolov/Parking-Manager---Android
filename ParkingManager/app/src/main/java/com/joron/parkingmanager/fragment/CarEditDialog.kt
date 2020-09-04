package com.joron.parkingmanager.fragment

import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
import android.view.inputmethod.InputMethodManager
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
class CarEditDialog : DialogFragment(){
    private var onInputDone: ((plate: String) -> Unit)? = null
    private var onCarDeleteClick: (() -> Unit?)? = null
    private var dialogType = -1
    private var carToDelete: Car? = null
    private lateinit var editText: AppCompatEditText

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
        editText = view.findViewById(R.id.plateInput)
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
        editText.showSoftInputOnFocus = true
        if (editText.requestFocus()) {
            showKeyboard(editText)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        hideKeyboard()
        super.onDismiss(dialog)
    }

    private fun showKeyboard(editText: AppCompatEditText) {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        dialog?.window?.setSoftInputMode(SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED)
    }

    private fun hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(editText.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
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