package com.doctor.eprescription.data.remote.model.request

import com.google.gson.annotations.SerializedName

data class GetPrescriptionDetailRequest(
    @SerializedName("doctorId") val doctorId: Int,
    @SerializedName("patientId") val patientId: String,
    @SerializedName("messageId") val messageId: String,
    @SerializedName("prescriptionMedicineId") val prescriptionMedicineId: String,
)
