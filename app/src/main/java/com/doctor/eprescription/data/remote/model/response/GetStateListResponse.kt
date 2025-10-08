package com.doctor.eprescription.data.remote.model.response

import com.google.gson.annotations.SerializedName

data class GetStateListResponse(
    @SerializedName("callStatus") val callStatus: String,
    @SerializedName("resultCode") val resultCode: String,
    @SerializedName("resultDesc") val resultDesc: String,
    @SerializedName("data") val data: Data
) {
    data class Data(
        @SerializedName("states") val states: List<State>
    )

    data class State(
        @SerializedName("id") val id: Int,
        @SerializedName("name") val name: String,
        @SerializedName("abbreviation") val abbreviation: String
    )
}
