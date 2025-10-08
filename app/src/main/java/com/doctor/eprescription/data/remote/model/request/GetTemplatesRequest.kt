package com.doctor.eprescription.data.remote.model.request

import com.google.gson.annotations.SerializedName

data class GetTemplatesRequest(
    @SerializedName("doctorId") val doctorId: Int?,
    @SerializedName("patientId") val patientId: Int?,
    @SerializedName("templateType") val templateType: Int?,
    @SerializedName("organizationId") val organizationId: Int? = 0,
    @SerializedName("specialityId") val specialityId: Int? = 34,
)
