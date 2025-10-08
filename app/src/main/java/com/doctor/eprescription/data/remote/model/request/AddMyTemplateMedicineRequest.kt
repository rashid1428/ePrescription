package com.doctor.eprescription.data.remote.model.request

import com.google.gson.annotations.SerializedName

data class AddMyTemplateMedicineRequest(
    @SerializedName("doctorId") val doctorId: String,
    @SerializedName("patientId") val patientId: String,
    @SerializedName("myTemplateMedicineIdList") val myTemplateMedicineIdList: List<Int>,
)
