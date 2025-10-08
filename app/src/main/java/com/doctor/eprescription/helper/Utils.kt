package com.doctor.eprescription.helper

fun hasValue(givenString: String?): Boolean {
    return try {
        givenString != null && !givenString.equals(
            "null",
            ignoreCase = true
        ) && !givenString.equals(
            "",
            ignoreCase = true
        )
    } catch (e: Exception) {
        false
    }//try-catch ends
}//hasValue ends