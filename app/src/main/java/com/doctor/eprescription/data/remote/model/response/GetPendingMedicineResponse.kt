package com.doctor.eprescription.data.remote.model.response

import com.doctor.eprescription.data.remote.model.response.common.MedicineData
import com.google.gson.annotations.SerializedName

data class GetPendingMedicineResponse(
    @SerializedName("callStatus") val callStatus: String,
    @SerializedName("resultCode") val resultCode: String,
    @SerializedName("resultDesc") val resultDesc: String,
    @SerializedName("data") val data: Data?
) {
    data class Data(
        @SerializedName("medicinesList") val medicinesList: List<MedicineData>
    )

    /*data class Medicine(
        @SerializedName("title") val title: String?,
        @SerializedName("requestType") val requestType: Int,
        @SerializedName("isControlledSubstance") val isControlledSubstance: Int,
        @SerializedName("pendingMedicineId") val pendingMedicineId: Int,
        @SerializedName("medicineId") val medicineId: Int,
        @SerializedName("medicineName") val medicineName: String?,
        @SerializedName("dosage") val dosage: String,
        @SerializedName("doseUnit") val doseUnit: String,
        @SerializedName("route") val route: String,
        @SerializedName("direction") val direction: String,
        @SerializedName("quantity") val quantity: String,
        @SerializedName("quantityUnitId") val quantityUnitId: String,
        @SerializedName("quantityUnit") val quantityUnit: String,
        @SerializedName("refill") val refill: String,
        @SerializedName("rxcui") val rxcui: String?,
        @SerializedName("maySubstitute") val maySubstitute: Boolean,
        @SerializedName("addToTemplate") val addToTemplate: Boolean,
        @SerializedName("prn") val prn: Boolean,
        @SerializedName("specialInstructions") val specialInstructions: String,
        @SerializedName("freeText") val freeText: Int,
        @SerializedName("sig") val sig: String,
        @SerializedName("combineSig") val combineSig: String,
        @SerializedName("icdCodeId") val icdCodeId: Int,
        @SerializedName("icdCode") val icdCode: String?,
        @SerializedName("icdCodeDescription") val icdCodeDescription: String?,
        @SerializedName("strengthFormId") val strengthFormId: Int,
        @SerializedName("strengthFormName") val strengthFormName: String,
        @SerializedName("compoundMedicinesList") val compoundMedicinesList: List<CompoundMedicine>
    )

    data class CompoundMedicine(
        @SerializedName("id") val id: Int,
        @SerializedName("medicineId") val medicineId: Int,
        @SerializedName("medicineName") val medicineName: String,
        @SerializedName("productCode") val productCode: String?,
        @SerializedName("productQualifier") val productQualifier: String?,
        @SerializedName("quantity") val quantity: String,
        @SerializedName("quantityCodeListQualifier") val quantityCodeListQualifier: String?,
        @SerializedName("quantityUnitId") val quantityUnitId: Int,
        @SerializedName("quantityUnit") val quantityUnit: String
    )*/
}

