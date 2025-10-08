package com.doctor.eprescription.data.remote.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class GetMenuResponse(
    @SerializedName("callStatus") val callStatus: String,
    @SerializedName("resultCode") val resultCode: String,
    @SerializedName("resultDesc") val resultDesc: String,
    @SerializedName("data") val data: Data
) : Parcelable {

    @Parcelize
    data class Data(
        @SerializedName("pharmacy") val pharmacy: Pharmacy,
        @SerializedName("foodAllergies") val foodAllergies: List<FoodAllergy>?,
        @SerializedName("drugAllergies") val drugAllergies: List<String>?,
        @SerializedName("oldPharmaciesList") val oldPharmaciesList: List<Pharmacy>
    ) : Parcelable

}

@Parcelize
data class FoodAllergy(
    @SerializedName("id") val id: Int,
//    @SerializedName("isOtherFoodAllergy") val _isOtherFoodAllergy: String?,
    @SerializedName("isOther", alternate = ["isOtherFoodAllergy"]) val isOther: String? = "false",
    @SerializedName("name") val name: String,
    @SerializedName("otherFoodAllergy") val _otherFoodAllergy: String? = ""
) : Parcelable {
//    val isOtherFoodAllergy get() = _isOtherFoodAllergy ?: "false"
    val otherFoodAllergy get() = _otherFoodAllergy.orEmpty()
}

@Parcelize
data class Pharmacy(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("street") val street: String,
    @SerializedName("city") val city: String,
    @SerializedName("state") val state: String,
    @SerializedName("zipCode") val zipCode: String,
    @SerializedName("specialtyName") val specialtyName: String?,
    @SerializedName("mailOrder") val mailOrder: Boolean,
    @SerializedName("retail") val retail: Boolean,
    val _isSelected: Boolean? = false
) : Parcelable {
    fun search(query: String): Boolean {
        return name.contains(query, true) ||
                street.contains(query, true) ||
                city.contains(query, true) ||
                state.contains(query, true)
    }

    val isSelected get() = _isSelected ?: false

    constructor(id: Int, name: String) : this(id, name, "", "", "", "", "", false, false)
}
