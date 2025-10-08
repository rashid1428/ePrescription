package com.doctor.eprescription.data.remote.model.request

import com.google.gson.annotations.SerializedName

data class UpdatePendingMedicineRequest(
    @SerializedName("addToTemplate") val addToTemplate: Int,
    @SerializedName("direction") val direction: String,
    @SerializedName("dosage") val dosage: String,
    @SerializedName("doseUnit") val doseUnit: String,
    @SerializedName("freeText") val freeText: Int,
    @SerializedName("icdCodeId") val icdCodeId: String,
    @SerializedName("isControlledSubstance") val isControlledSubstance: String,
    @SerializedName("isPendingMedicine") val isPendingMedicine: Int,
    @SerializedName("maySubstitute") val maySubstitute: Int,
    @SerializedName("medicineId") val medicineId: String,
    @SerializedName("medicineName") val medicineName: String,
    @SerializedName("pendingMedicineId") val pendingMedicineId: String,
    @SerializedName("prn") val prn: Int,
    @SerializedName("quantity") val quantity: String,
    @SerializedName("refill") val refill: String,
    @SerializedName("requestType") val requestType: Int,
    @SerializedName("route") val route: String,
    @SerializedName("sig") val sig: String,
    @SerializedName("specialInstructions") val specialInstructions: String,
    @SerializedName("strengthFormId") val strengthFormId: String?,
    @SerializedName("title") val title: String,
    @SerializedName("totalQuantityUnit") val totalQuantityUnit: String,
    @SerializedName("compoundMedicinesList") val compoundMedicinesList: List<String>?
)
