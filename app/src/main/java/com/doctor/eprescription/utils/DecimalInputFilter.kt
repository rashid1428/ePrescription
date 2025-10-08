package com.doctor.eprescription.utils

import android.text.InputFilter
import android.text.Spanned

class DecimalInputFilter : InputFilter {
    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {
        val builder = StringBuilder(dest)
        builder.replace(dstart, dend, source?.subSequence(start, end).toString())

        val input = builder.toString()
        if (input == "0.0") {
            // Remove the leading zero if the input is "0.0"
            return ""
        }

        if (input.startsWith(".")) {
            return "0$input"
        }

        // Allow the input if it matches the desired decimal format and is not "1.00"
        if (input.matches(Regex("^(?!1\\.00)\\d{0,3}(\\.\\d{0,4})?$"))) {
            return null // Accept the input as is
        }

        // Reject the input if it doesn't match the desired format
        return ""
    }
}
