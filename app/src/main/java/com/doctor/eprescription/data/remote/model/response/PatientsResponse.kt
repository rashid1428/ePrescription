package com.doctor.eprescription.data.remote.model.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PatientsResponse(
    @SerializedName("callStatus") val callStatus: String,
    @SerializedName("resultCode") val resultCode: String,
    @SerializedName("resultDesc") val resultDesc: String,
    @SerializedName("data") val data: Data?
) : Parcelable {

    @Parcelize
    data class Data(
        @SerializedName("patients") val patients: List<Patient>?
    ) : Parcelable

    @Parcelize
    data class Patient(
        @SerializedName("patientId") val patientId: Int,
        @SerializedName("firstName") val firstName: String,
        @SerializedName("lastName") val lastName: String,
        @SerializedName("email") val _email: String?,
        @SerializedName("gender") val gender: String,
        @SerializedName("dateOfBirth") val dateOfBirth: String,
        @SerializedName("weight") val _weight: String?,
        @SerializedName("weightUnit") val _weightUnit: String?,
        @SerializedName("height") val _height: String?,
        @SerializedName("heightUnit") val _heightUnit: String?,
        @SerializedName("phone") val phone: String,
        @SerializedName("street") val street: String,
        @SerializedName("city") val city: String,
        @SerializedName("zipCode") val zipCode: String,
        @SerializedName("state") val state: State,
        @SerializedName("pharmacyId") val pharmacyId: Int,
        @SerializedName("pharmacyName") val pharmacyName: String,
        @SerializedName("doctorId") val doctorId: Int
    ) : Parcelable {
        fun search(query: String): Boolean {
            return firstName.contains(query, true) ||
                    lastName.contains(query, true) ||
                    dateOfBirth.contains(query, true) ||
                    phone.contains(query, true)
        }

        val height get() = _height.orEmpty()
        val heightUnit get() = _heightUnit.orEmpty()
        val weight get() = _weight.orEmpty()
        val weightUnit get() = _weightUnit.orEmpty()
        val email get() = _email.orEmpty()
        val fullName get() = "$firstName $lastName"
    }

    @Parcelize
    data class State(
        @SerializedName("id") val id: Int,
        @SerializedName("name") val name: String,
        @SerializedName("abbreviation") val abbreviation: String
    ) : Parcelable
}

