package com.doctor.eprescription.data.remote.model.request

import com.google.gson.annotations.SerializedName

data class AddPendingMedicineRequest(
    @SerializedName("doctorId") val doctorId: Int,
    @SerializedName("patientId") val patientId: Int,
    @SerializedName("requestType") val requestType: Int,
    @SerializedName("medicineList") val medicineList: List<Medicine>,
) {
    data class Medicine(
        @SerializedName("isControlledSubstance") val isControlledSubstance: String,
        @SerializedName("title") val title: String?,
        @SerializedName("medicineId") val medicineId: String,
        @SerializedName("medicineName") val medicineName: String,
        @SerializedName("dosage") val dosage: String,
        @SerializedName("doseUnit") val doseUnit: String,
        @SerializedName("route") val route: String,
        @SerializedName("direction") val direction: String,
        @SerializedName("quantity") val quantity: String,
        @SerializedName("totalQuantityUnit") val totalQuantityUnit: String,
        @SerializedName("strengthFormId") val strengthFormId: String?,
        @SerializedName("icdCodeId") val icdCodeId: String,
        @SerializedName("refill") val refill: String,
        @SerializedName("maySubstitute") val maySubstitute: Int,
        @SerializedName("addToTemplate") val addToTemplate: Int,
        @SerializedName("prn") val prn: Int,
        @SerializedName("specialInstructions") val specialInstructions: String,
        @SerializedName("freeText") val freeText: Int,
        @SerializedName("sig") val _sig: String?,
        @SerializedName("compoundMedicinesList") val compoundMedicinesList: List<String>,
        @SerializedName("isHomeMed") val isHomeMed: Boolean = false,
    ) {
        val sig get() = _sig.orEmpty()
    }
}
