package com.doctor.eprescription.data.remote.model.request

import com.google.gson.annotations.SerializedName

data class GetSigRequest(
    @SerializedName("doctorId") val doctorId: String,
    @SerializedName("medicineId") val medicineId: String,
    @SerializedName("medicineName") val medicineName: String,
)
