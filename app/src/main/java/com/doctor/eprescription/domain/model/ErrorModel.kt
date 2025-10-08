package com.doctor.eprescription.domain.model

data class ErrorModel(
    val title: String,
    val message: String,
    val code: Int = 0,
)
