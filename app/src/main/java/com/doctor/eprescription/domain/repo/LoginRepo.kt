package com.doctor.eprescription.domain.repo

import com.doctor.eprescription.data.remote.Resource
import com.doctor.eprescription.data.remote.model.request.LoginRequest
import com.doctor.eprescription.data.remote.model.response.LoginResponse

interface LoginRepo {

    suspend fun login(email: String, loginRequest: LoginRequest): Resource<LoginResponse>

}