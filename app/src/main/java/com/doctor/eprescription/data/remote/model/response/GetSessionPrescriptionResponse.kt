package com.doctor.eprescription.data.remote.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class GetSessionPrescriptionResponse(
    @SerializedName("callStatus") val callStatus: String,
    @SerializedName("resultCode") val resultCode: String,
    @SerializedName("resultDesc") val resultDesc: String,
    @SerializedName("data") val data: MedicinesData
) {
    data class MedicinesData(
        @SerializedName("medicinesList") val medicinesList: List<Medicine>
    )

    @Parcelize
    data class Medicine(
        @SerializedName("messageId") val messageId: String,
        @SerializedName("patientId") val patientId: Int,
        @SerializedName("doctorId") val doctorId: Int,
        @SerializedName("doctorFirstName") val doctorFirstName: String,
        @SerializedName("doctorLastName") val doctorLastName: String,
        @SerializedName("pharmacyId") val pharmacyId: Int,
        @SerializedName("pharmacyName") val pharmacyName: String,
        @SerializedName("prescriptionId") val prescriptionId: Int,
        @SerializedName("prescriptionMedicineId") val prescriptionMedicineId: Int,
        @SerializedName("medicineName") val medicineName: String,
        @SerializedName("quantityValue") val quantityValue: String,
        @SerializedName("quantityUnitOfMeasure") val quantityUnitOfMeasure: String,
        @SerializedName("numberOfRefills") val numberOfRefills: Int,
        @SerializedName("specialInstructions") val specialInstructions: String,
        @SerializedName("sig") val sig: String,
        @SerializedName("daysSupply") val daysSupply: Int,
        @SerializedName("substitutions") val substitutions: Int,
        @SerializedName("writtenDate") val writtenDate: String,
        @SerializedName("statusId") val statusId: Int,
        @SerializedName("statusType") val statusType: String,
        @SerializedName("statusMessage") val statusMessage: String,
        @SerializedName("statusCode") val statusCode: String,
        @SerializedName("initiatedByPharmacyId") val initiatedByPharmacyId: Int,
        @SerializedName("isRequestDisable") val isRequestDisable: Int
    ): Parcelable
}
