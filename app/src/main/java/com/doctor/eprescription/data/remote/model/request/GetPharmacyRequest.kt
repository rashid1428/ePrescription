package com.doctor.eprescription.data.remote.model.request

import com.google.gson.annotations.SerializedName

data class GetPharmacyRequest(
    @SerializedName("businessName") val businessName: String = "",
    @SerializedName("city") val city: String = "",
    @SerializedName("state") val state: String = "",
    @SerializedName("zipCode") val zipCode: String = "",
    @SerializedName("mailOrder") val mailOrder: String = "0",
    @SerializedName("speciality") val speciality: String = "",
    @SerializedName("startRow") val startRow: String = "0",
    @SerializedName("pageSize") val pageSize: String = "100"
)
