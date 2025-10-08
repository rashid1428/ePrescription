package com.doctor.eprescription.data.remote.model.request

import com.google.gson.annotations.SerializedName

data class DeletePendingMedicineRequest(
//    @SerializedName("patientId") val patientId: Int?,
//    @SerializedName("doctorId") val doctorId: Int?,
    @SerializedName("medicineIds") val medicineId: List<Int>?,
)
