package com.doctor.eprescription.data.remote.model.response

import com.google.gson.annotations.SerializedName

data class GetQuantitiesResponse(
    @SerializedName("callStatus") val callStatus: String,
    @SerializedName("resultCode") val resultCode: String,
    @SerializedName("resultDesc") val resultDesc: String,
    @SerializedName("data") val data: Data
) {
    data class Data(
        @SerializedName("quantities") val quantities: List<Quantity>
    )

    data class Quantity(
        @SerializedName("id") val id: Int,
        @SerializedName("name") val name: String,
        @SerializedName("code") val code: String
    )
}
