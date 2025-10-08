package com.doctor.eprescription.data.remote.model.response

import android.os.Parcelable
import com.doctor.eprescription.data.remote.model.response.common.MedicineData
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class TemplatesResponse(
    @SerializedName("callStatus") val callStatus: Boolean,
    @SerializedName("resultCode") val resultCode: String,
    @SerializedName("resultDesc") val resultDesc: String,
    @SerializedName("data") val data: MedicinesData?
) : Parcelable {

    @Parcelize
    data class MedicinesData(
        @SerializedName("medicinesList") val medicinesList: List<MedicineData>?
    ) : Parcelable


}

