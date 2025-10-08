package com.doctor.eprescription.domain.interactors

import android.util.Patterns

class EmailValidationUseCase {


    fun isValid(email: String): Boolean {
        return if (email.isEmpty()) false
        else Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    }
}