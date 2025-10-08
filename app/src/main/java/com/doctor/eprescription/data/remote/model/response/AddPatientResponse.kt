package com.doctor.eprescription.data.remote.model.response

import com.google.gson.annotations.SerializedName

data class AddPatientResponse(
    @SerializedName("callStatus") val callStatus: String,
    @SerializedName("resultCode") val resultCode: String,
    @SerializedName("resultDesc") val resultDesc: String,
    @SerializedName("data") val patient: PatientsResponse.Patient?
)
