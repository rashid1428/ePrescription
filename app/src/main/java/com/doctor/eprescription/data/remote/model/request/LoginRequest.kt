package com.doctor.eprescription.data.remote.model.request

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("password") val password: String
)
