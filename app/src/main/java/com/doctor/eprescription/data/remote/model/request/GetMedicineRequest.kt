package com.doctor.eprescription.data.remote.model.request

import com.google.gson.annotations.SerializedName

data class GetMedicineRequest(
    @SerializedName("searchString") val searchString: String,
    @SerializedName("dosage") val dosage: String,
    @SerializedName("strength") val strength: String,
    @SerializedName("startRow") val startRow: String,
    @SerializedName("pageSize") val pageSize: String
)
