package com.doctor.eprescription.basefragment

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.doctor.eprescription.R

abstract class FullScreenDialogFragment<VB : ViewBinding>(private val inflate: Inflate<VB>) :
    BaseDialogFragmentVM<VB>(inflate) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NO_TITLE, R.style.MyStyle)
    }
}