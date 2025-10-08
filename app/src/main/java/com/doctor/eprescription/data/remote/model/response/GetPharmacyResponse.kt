package com.doctor.eprescription.data.remote.model.response

import com.google.gson.annotations.SerializedName

data class GetPharmacyResponse(
    @SerializedName("callStatus") val callStatus: String,
    @SerializedName("resultCode") val resultCode: String,
    @SerializedName("resultDesc") val resultDesc: String,
    @SerializedName("data") val data: PharmacyData
) {
    data class PharmacyData(
        @SerializedName("pharmacyData") val pharmacyData: List<Pharmacy>
    )
}
