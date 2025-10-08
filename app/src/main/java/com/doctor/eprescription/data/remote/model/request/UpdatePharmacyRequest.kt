package com.doctor.eprescription.data.remote.model.request

import com.google.gson.annotations.SerializedName

data class UpdatePharmacyRequest(
    @SerializedName("patientId") val patientId: String,
    @SerializedName("pharmacyId") val pharmacyId: String,
)
