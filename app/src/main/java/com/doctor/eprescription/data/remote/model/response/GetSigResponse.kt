package com.doctor.eprescription.data.remote.model.response

import com.google.gson.annotations.SerializedName

data class GetSigResponse(
    @SerializedName("callStatus") val callStatus: String,
    @SerializedName("resultCode") val resultCode: String,
    @SerializedName("resultDesc") val resultDesc: String,
    @SerializedName("data") val data: SigDetail
) {
    data class SigDetail(
        @SerializedName("sig") val sig: SigContent
    )

    data class SigContent(
        @SerializedName("dose") val dose: String,
        @SerializedName("doseUnitId") val doseUnitId: Int,
        @SerializedName("doseUnit") val doseUnit: String?,
        @SerializedName("routeId") val routeId: Int,
        @SerializedName("route") val route: String?,
        @SerializedName("directionId") val directionId: Int,
        @SerializedName("direction") val direction: String?,
        @SerializedName("sigText") val sigText: String,
        @SerializedName("quantityUnitId") val quantityUnitId: Int,
        @SerializedName("quantityUnit") val quantityUnit: String,
        @SerializedName("quantity") val quantity: String
    )
}