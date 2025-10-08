package com.doctor.eprescription.data.remote.model.response

import com.google.gson.annotations.SerializedName

data class AddPatientRequest(
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("street") val street: String,
    @SerializedName("city") val city: String,
    @SerializedName("stateId") val stateId: String,
    @SerializedName("pharmacyId") val pharmacyId: String,
    @SerializedName("zipCode") val zipCode: String,
    @SerializedName("dateOfBirth") val dateOfBirth: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("weight") val weight: String,
    @SerializedName("weightUnitId") val weightUnitId: Int,
    @SerializedName("height") val height: String,
    @SerializedName("heightUnitId") val heightUnitId: Int,
    @SerializedName("doctorId") val doctorId: Int
)
