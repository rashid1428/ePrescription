package com.doctor.eprescription.extension

import androidx.appcompat.app.AlertDialog

fun AlertDialog.visibility(visible: Boolean) {
    if (visible) show() else dismiss()
}

fun AlertDialog?.safeDismiss() {
    this?.let {
        if (it.isShowing) {
            it.dismiss()
        }
    }
}

fun AlertDialog?.safeShow() {
    this?.let {
        if (it.isShowing.not()) {
            it.show()
        }
    }
}