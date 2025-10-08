package com.doctor.eprescription.data.remote.model.request

import com.google.gson.annotations.SerializedName

data class CancelErxRequest(
    @SerializedName("doctorId") val doctorId: Int,
    @SerializedName("patientId") val patientId: Int,
    @SerializedName("pharmacyId") val pharmacyId: String,
    @SerializedName("prescriptionId") val prescriptionId: Int,
    @SerializedName("medicines") val medicines: List<Int>
)
