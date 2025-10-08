package com.doctor.eprescription.data.remote.model.data

import android.os.Parcelable
import com.doctor.eprescription.data.remote.model.response.FoodAllergy
import com.doctor.eprescription.data.remote.model.response.GetMenuResponse
import kotlinx.parcelize.Parcelize

@Parcelize
data class AllergiesData(
    val foodAllergies: List<FoodAllergy>?,
    val drugAllergies: List<String>?
) : Parcelable
