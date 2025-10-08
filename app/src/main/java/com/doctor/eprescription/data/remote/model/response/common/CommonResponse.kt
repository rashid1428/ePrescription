package com.doctor.eprescription.data.remote.model.response.common

import com.google.gson.annotations.SerializedName

data class CommonResponse(
    @SerializedName("callStatus") val callStatus: String,
    @SerializedName("resultCode") val resultCode: String,
    @SerializedName("resultDesc") val resultDesc: String
)
