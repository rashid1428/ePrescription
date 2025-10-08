package com.doctor.eprescription.domain.model

import com.doctor.eprescription.constants.MenuOptions

data class PatientMenuItem(
    val icon: Int,
    val name: String,
    val id: MenuOptions
)
