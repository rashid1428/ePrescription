package com.doctor.eprescription.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.doctor.eprescription.basefragment.BaseDialogFragmentVB
import com.doctor.eprescription.databinding.DialogConfirmationBinding

class ConfirmationDialogFragment :
    BaseDialogFragmentVB<DialogConfirmationBinding>(DialogConfirmationBinding::inflate) {

    private var callBack: ((action: Boolean) -> Unit)? = null

    companion object {

        fun show(fragmentManager: FragmentManager, callBack: ((action: Boolean) -> Unit)?) {
            val dialog = ConfirmationDialogFragment()
            dialog.callBack = callBack
            val className = Thread.currentThread().stackTrace[3].className.substringAfterLast(".")
            dialog.show(fragmentManager, className)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnNegative.setOnClickListener {
                callBack?.invoke(false)
                dismiss()
            }

            btnPositive.setOnClickListener {
                callBack?.invoke(true)
                dismiss()
            }
        }
    }

}