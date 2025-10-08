package com.doctor.eprescription.features.homescreen.prescription.createprescription.model

import com.doctor.eprescription.extension.generateRandomString

data class CompoundingModel(
    val medicine: String,
    val quantity: Int,
    val quantityUnit: String,
    val id: String = generateRandomString()
) {
    constructor() : this("", 0, "")
}


