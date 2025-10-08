package com.doctor.eprescription.data.remote.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class FoodAndAllergiesListResponse(
    @SerializedName("callStatus") val callStatus: String,
    @SerializedName("resultCode") val resultCode: String,
    @SerializedName("resultDesc") val resultDesc: String,
    @SerializedName("data") val data: AllergyData
) {
    data class AllergyData(
        @SerializedName("foodAllergies") val foodAllergies: List<FoodAllergy>
    )

    @Parcelize
    data class FoodAllergy(
        @SerializedName("id") val id: Int,
        @SerializedName("isOther") val isOther: String? = "false",
        @SerializedName("name") val name: String,
        @SerializedName("isOtherFoodAllergy") val _otherFoodAllergy: String? = ""
    ) : Parcelable {
        val otherFoodAllergy get() = _otherFoodAllergy.orEmpty()
    }
}
