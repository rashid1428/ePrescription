package com.doctor.eprescription.data.remote.model.response

import com.doctor.eprescription.data.remote.model.response.common.MedicineData
import com.google.gson.annotations.SerializedName

data class SendMedicineRequest(
    @SerializedName("doctorId") val doctorId: Int,
    @SerializedName("patientId") val patientId: String,
    @SerializedName("pharmacyId") val pharmacyId: String,
    @SerializedName("controlledSubstanceExists") val controlledSubstanceExists: Boolean?,
    @SerializedName("verified") val verified: Boolean?,
//    @SerializedName("medicines") val medicines: List<MedicineData>,
)
