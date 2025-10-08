package com.doctor.eprescription.data.remote.model.response

import com.google.gson.annotations.SerializedName

data class DeletePatientResponse(
    @SerializedName("callStatus") val callStatus: String,
    @SerializedName("resultCode") val resultCode: String,
    @SerializedName("resultDesc") val resultDesc: String,
) {
}