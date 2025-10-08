package com.doctor.eprescription.data.remote.model.response

import com.google.gson.annotations.SerializedName

data class GetMedicineResponse(
    @SerializedName("callStatus") val callStatus: String,
    @SerializedName("resultCode") val resultCode: String,
    @SerializedName("resultDesc") val resultDesc: String,
    @SerializedName("data") val data: MedicineData?
) {
    data class Medicine(
        @SerializedName("id") val id: String,
        @SerializedName("name") val name: String,
        @SerializedName("strength") val strength: String,
        @SerializedName("strengthForm") val strengthForm: String,
        @SerializedName("routeOfAdministration") val routeOfAdministration: String?,
        @SerializedName("strengthUnitOfMeasure") val strengthUnitOfMeasure: String,
        @SerializedName("ndc") val ndc: String,
        @SerializedName("productCode") val productCode: String,
        @SerializedName("productCodeQualifier") val productCodeQualifier: String,
        @SerializedName("upc") val upc: String,
        @SerializedName("drugQualifier") val drugQualifier: String?,
        @SerializedName("isControlledSubstance") val isControlledSubstance: Int
    ) {
        constructor(id: String, name: String) : this(
            id = id,
            name = name,
            strength = "",
            strengthForm = "",
            routeOfAdministration = "",
            strengthUnitOfMeasure = "",
            ndc = "",
            productCode = "",
            productCodeQualifier = "",
            upc = "",
            drugQualifier = "",
            isControlledSubstance = 0
        )
    }

    data class MedicineData(
        @SerializedName("medicines") val medicines: List<Medicine>
    )
}
