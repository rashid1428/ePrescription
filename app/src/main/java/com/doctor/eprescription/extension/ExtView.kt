package com.doctor.eprescription.extension

import android.view.View
import android.widget.TextView
import com.doctor.eprescription.customviews.FormEditText

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun goneViews(vararg views: View) {
    views.forEach {
        it.gone()
    }
}

fun visibleViews(vararg views: View) {
    views.forEach {
        it.visible()
    }
}

fun FormEditText.setTextNotEmpty(text: String?) {
    if (text == null) return
    if (text.isNotEmpty() && text.isNotBlank()) {
        setText(text)
    }
    if (isFocused) {
        getEditText().setSelection(text.length)
    }
}