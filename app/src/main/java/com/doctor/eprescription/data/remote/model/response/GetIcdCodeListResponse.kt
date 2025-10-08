package com.doctor.eprescription.data.remote.model.response

import com.google.gson.annotations.SerializedName

data class GetIcdCodeListResponse(
    @SerializedName("callStatus") val callStatus: String,
    @SerializedName("resultCode") val resultCode: String,
    @SerializedName("resultDesc") val resultDesc: String,
    @SerializedName("data") val data: IcdCodeListData?
) {
    data class IcdCode(
        @SerializedName("id") val id: Int,
        @SerializedName("code") val code: String,
        @SerializedName("description") val description: String
    ) {
        val getFullText
            get() = if (description.isEmpty()) "" else description.plus(" (").plus(code).plus(")")
    }

    data class IcdCodeListData(
        @SerializedName("icdCodes") val icdCodes: List<IcdCode>?
    )
}