package com.doctor.eprescription.extension

val String.Companion.EMPTY: String
    get() = ""

fun String?.isNotEmpty(action: (it: String) -> Unit) {
    if (this == null) return
    if (this.isNotEmpty()) {
        action(this)
    }
}