package com.doctor.eprescription.data.remote.model.response

import com.google.gson.annotations.SerializedName

data class GetDoseListResponse(
    @SerializedName("callStatus") val callStatus: String,
    @SerializedName("resultCode") val resultCode: String,
    @SerializedName("resultDesc") val resultDesc: String,
    @SerializedName("data") val data: Data
) {
    data class Data(
        @SerializedName("dose") val dose: List<Dose>
    )

    data class Dose(
        @SerializedName("id") val id: Int,
        @SerializedName("name") val name: String,
        @SerializedName("code") val code: String? = null
    )
}
