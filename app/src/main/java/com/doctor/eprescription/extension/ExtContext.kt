package com.doctor.eprescription.extension

import android.content.Context
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.doctor.eprescription.R

fun Context.color(@ColorRes id: Int): Int {
    return ContextCompat.getColor(this, id)
}