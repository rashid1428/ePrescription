package com.doctor.eprescription.data.remote.model.response

import com.google.gson.annotations.SerializedName

data class GetDirectionResponse(
    @SerializedName("callStatus") val callStatus: String,
    @SerializedName("resultCode") val resultCode: String,
    @SerializedName("resultDesc") val resultDesc: String,
    @SerializedName("data") val data: Data
) {
    data class Data(
        @SerializedName("directions") val directions: List<Direction>
    )

    data class Direction(
        @SerializedName("id") val id: Int,
        @SerializedName("name") val name: String
    )
}
